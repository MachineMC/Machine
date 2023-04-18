package org.machinemc.server.entities;

import lombok.Getter;
import lombok.Setter;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.EntityType;
import org.machinemc.server.utils.EntityUtils;
import org.machinemc.api.utils.NBTUtils;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.Nullable;

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
    @Getter @Setter
    private Location location;
    @Getter @Setter
    private float fallDistance;
    @Getter @Setter
    private short remainingFireTicks;
    @Getter @Setter
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
        return location.getWorld();
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

    @Override
    public void init() {
        if (active)
            throw new IllegalStateException(this + " is already initiated");
        active = true;
        getServer().getEntityManager().addEntity(this);
        getWorld().spawn(this, location);
    }

    @Override
    public void remove() {
        if (!active)
            throw new IllegalStateException(this + " is not active");
        active = false;
        getServer().getEntityManager().removeEntity(this);
        getWorld().remove(this);
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

        getLocation().setX(pos.get(0));
        getLocation().setY(pos.get(1));
        getLocation().setZ(pos.get(2));
        getLocation().setYaw(rotation.get(0));
        getLocation().setPitch(rotation.get(1));

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
