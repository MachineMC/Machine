/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.entities;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.EntityType;
import org.machinemc.api.entities.Player;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.ServerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.NBTUtils;
import org.machinemc.api.world.EntityPosition;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.World;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.Machine;
import org.machinemc.server.network.packets.out.play.*;
import org.machinemc.server.utils.EntityUtils;

import java.util.*;

import static java.util.Map.entry;

/**
 * Default server entity implementation.
 */
public abstract class ServerEntity implements Entity {

    @Getter
    private final Machine server;

    @Getter
    private final EntityType entityType;
    @Getter
    private UUID uuid;
    @Getter
    private final int entityId;

    @Getter
    private boolean active;

    private final Set<String> tags = new HashSet<>();
    @Getter @Setter
    private boolean silent;
    @Getter @Setter
    private boolean noGravity;
    @Getter @Setter
    private boolean glowing;
    @Getter @Setter
    private boolean hasVisualFire;
    @Getter @Setter
    private int ticksFrozen;
    private Location location;
    @Getter
    private Location previousLocation;
    @Getter @Setter
    private float fallDistance;
    @Getter @Setter
    private short remainingFireTicks;
    @Getter
    private boolean onGround;
    @Getter @Setter
    private boolean invulnerable;
    @Getter @Setter
    private int portalCooldown;

    public ServerEntity(final Machine server, final EntityType entityType, final UUID uuid) {
        this.server = server;
        this.entityType = entityType;
        this.uuid = uuid;
        entityId = EntityUtils.getEmptyID();
        location = new Location(0, 0, 0, getServer().getDefaultWorld());
        active = false;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public String getName() {
        return entityType.getTypeName();
    }

    @Override
    public @Nullable Component getCustomName() {
        // TODO return custom name metadata
        return null;
    }

    @Override
    public void setCustomName(final @Nullable Component customName) {
        // TODO set custom name metadata
    }

    @Override
    public boolean isCustomNameVisible() {
        // TODO metadata
        return false;
    }

    @Override
    public void setCustomNameVisible(final boolean customNameVisible) {
        // TODO metadata
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public Set<String> getTags() {
        return Set.copyOf(tags);
    }

    @Override
    public boolean addTag(final String tag) {
        return tags.size() < 1024 && tags.add(tag);
    }

    @Override
    public boolean removeTag(final String tag) {
        return tags.remove(tag);
    }

    /**
     * @param location new location
     * @param setPreviousLocation whether the previous location should be updated
     */
    protected void setLocation(final Location location, final boolean setPreviousLocation) {
        if (setPreviousLocation)
            previousLocation = this.location;
        this.location = location;
    }

    /**
     * Changes the on ground state of the entity.
     * @param onGround if the entity is on ground
     */
    protected void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public void init() {
        if (active)
            throw new IllegalStateException(this + " is already initiated");
        active = true;
        getServer().getEntityManager().addEntity(this);
        getWorld().spawn(this);
    }

    @Override
    public void remove() {
        if (!active)
            throw new IllegalStateException(this + " is not active");
        active = false;
        getServer().getConnection().broadcastPacket(new PacketPlayOutRemoveEntities(new int[]{getEntityId()}));
        getServer().getEntityManager().removeEntity(this);
        getWorld().remove(this);
    }

    /**
     * Handles the movement of the entity.
     * @param position new position
     * @param onGround if the entity is on ground
     */
    public void handleMovement(final EntityPosition position, final boolean onGround) {

        final Location currentLocation = getLocation();

        final double deltaX = Math.abs(position.getX() - currentLocation.getX());
        final double deltaY = Math.abs(position.getY() - currentLocation.getY());
        final double deltaZ = Math.abs(position.getZ() - currentLocation.getZ());
        final float deltaYaw = Math.abs(position.getYaw() - currentLocation.getYaw());
        final float deltaPitch = Math.abs(position.getPitch() - currentLocation.getPitch());

        final boolean positionChange = deltaX + deltaY + deltaZ > 0;
        final boolean rotationChange = deltaYaw + deltaPitch > 0;

        if (!(positionChange || rotationChange))
            return;

        final PlayerConnection connection = this instanceof Player player
                ? player.getConnection()
                : null;
        final ServerConnection serverConnection = getServer().getConnection();

        if (deltaX > 8 || deltaY > 8 || deltaZ > 8) {
            final Packet teleportPacket = new PacketPlayOutTeleportEntity(getEntityId(), position, onGround);
            serverConnection.broadcastPacket(teleportPacket, connected -> connected != connection);
        } else {
            final Packet positionPacket;
            if (rotationChange) {
                positionPacket = new PacketPlayOutEntityPositionAndRotation(
                        getEntityId(),
                        previousLocation,
                        currentLocation,
                        onGround);

            } else {
                positionPacket = new PacketPlayOutEntityPosition(
                        getEntityId(),
                        previousLocation,
                        currentLocation,
                        onGround);
            }
            serverConnection.broadcastPacket(positionPacket, connected -> connected != connection);
        }

        if (rotationChange) {
            final Packet headRotationPacket = new PacketPlayOutHeadRotation(getEntityId(), position.getYaw());
            serverConnection.broadcastPacket(headRotationPacket, connected -> connected != connection);
        }

        handleOnGround(onGround);
        setLocation(Location.of(position, getWorld()), true);
    }

    /**
     * Handles the change of the on ground state of the entity.
     * @param onGround if the entity is on ground
     */
    public void handleOnGround(final boolean onGround) {
        setOnGround(onGround);
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound(Map.ofEntries(
                entry("Pos", NBTUtils.list(location.getX(), location.getY(), location.getZ())),
                entry("Motion", NBTUtils.list(0, 0, 0)), // TODO implement motion
                entry("Rotation", NBTUtils.list(location.getYaw(), location.getPitch())),
                entry("FallDistance", fallDistance),
                entry("Fire", remainingFireTicks),
                entry("Air", (short) 0),
                entry("OnGround", (byte) (onGround ? 1 : 0)),
                entry("Invulnerable", (byte) (invulnerable ? 1 : 0)),
                entry("PortalCooldown", portalCooldown),
                entry("WorldUUIDLeast", getWorld().getUuid().getLeastSignificantBits()),
                entry("WorldUUIDMost", getWorld().getUuid().getMostSignificantBits())
        ));
        compound.setUUID("UUID", uuid);
        if (getCustomName() != null)
            compound.set("CustomName", getCustomName().toJson());
        if (isCustomNameVisible())
            compound.set("CustomNameVisible", (byte) (isCustomNameVisible() ? 1 : 0));
        if (silent)
            compound.set("Silent", (byte) 1);
        if (noGravity)
            compound.set("NoGravity", (byte) 1);
        if (glowing)
            compound.set("Glowing", (byte) 1);
        if (ticksFrozen > 0)
            compound.set("TicksFrozen", (byte) ticksFrozen);
        if (hasVisualFire)
            compound.set("HasVisualFire", (byte) 1);
        if (!tags.isEmpty())
            compound.set("Tags", new NBTList(tags.stream().toList()));
        return compound;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(final NBTCompound nbtCompound) {
        List<Double> pos = (List<Double>) ((NBTList) nbtCompound.get("Pos")).revert();
        List<Double> motion = (List<Double>) ((NBTList) nbtCompound.get("Motion")).revert();
        List<Float> rotation = (List<Float>) ((NBTList) nbtCompound.get("Rotation")).revert();

        if (pos.size() != 3)
            pos = new LinkedList<>(List.of(0d, 0d, 0d));
        if (motion.size() != 3)
            motion = new LinkedList<>(List.of(0d, 0d, 0d));
        if (rotation.size() != 2)
            rotation = new LinkedList<>(List.of(0f, 0f));

        final Location location = Location.of(
                pos.get(0), pos.get(1),
                pos.get(2),
                rotation.get(0),
                rotation.get(1),
                getWorld()
        );
        setLocation(location, false);

        setFallDistance(nbtCompound.getValue("FallDistance", 0f));
        setRemainingFireTicks(nbtCompound.getValue("Fire", (short) 0));
        setOnGround(nbtCompound.getValue("OnGround", (byte) 0) == 1);
        setInvulnerable(nbtCompound.getValue("Invulnerable", (byte) 0) == 1);
        setPortalCooldown(nbtCompound.getValue("PortalCooldown", 0));
        if (nbtCompound.containsKey("UUID"))
            uuid = nbtCompound.getUUID("UUID");
        if (nbtCompound.containsKey("CustomName")) {
            final String string = nbtCompound.getValue("CustomName");
            setCustomName(getServer().getComponentSerializer().deserializeJson(string));
        }
        setCustomNameVisible(nbtCompound.getValue("CustomNameVisible", 0) == 1);
        setSilent(nbtCompound.getValue("Silent", 0) == 1);
        setNoGravity(nbtCompound.getValue("NoGravity", 0) == 1);
        setGlowing(nbtCompound.getValue("Glowing", 0) == 1);
        setTicksFrozen(nbtCompound.getValue("TicksFrozen", 0));
        setHasVisualFire(nbtCompound.getValue("HasVisualFire", 0) == 1);
        if (nbtCompound.containsKey("Tags")) {
            tags.clear();
            final List<String> nbtStrings = (List<String>) ((NBTList) nbtCompound.get("Tags")).revert();
            final int i = Math.min(nbtStrings.size(), 1024);
            for (int j = 0; j < i; j++)
                tags.add(nbtStrings.get(j));
        }
    }

}
