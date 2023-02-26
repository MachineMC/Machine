package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.EntityUtils;
import me.pesekjak.machine.utils.NBTUtils;
import me.pesekjak.machine.utils.UUIDUtils;
import me.pesekjak.machine.world.Location;
import me.pesekjak.machine.world.World;
import mx.kenzie.nbt.NBTCompound;
import mx.kenzie.nbt.NBTList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

/**
 * Default server entity implementation.
 */
public abstract class ServerEntity implements Entity {

    @Getter
    private final @NotNull Machine server;

    @Getter
    private final @NotNull EntityType entityType;
    @Getter
    private @NotNull UUID uuid;
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
    private @NotNull Location location;
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

    public ServerEntity(@NotNull Machine server, @NotNull EntityType entityType, @NotNull UUID uuid) {
        this.server = server;
        this.entityType = entityType;
        this.uuid = uuid;
        entityId = EntityUtils.getEmptyID();
        location = new Location(0, 0, 0, getServer().getDefaultWorld());
        active = false;
    }

    @Override @NotNull
    public UUID uuid() {
        return uuid;
    }

    @Override
    public @NotNull String getName() {
        return entityType.getTypeName();
    }

    @Override
    public @Nullable Component getCustomName() {
        // TODO return custom name metadata
        return null;
    }

    @Override
    public void setCustomName(@Nullable Component customName) {
        // TODO set custom name metadata
    }

    @Override
    public boolean isCustomNameVisible() {
        // TODO metadata
        return false;
    }

    @Override
    public void setCustomNameVisible(boolean customNameVisible) {
        // TODO metadata
    }

    @Override
    public @NotNull World getWorld() {
        return location.getWorld();
    }

    @Override
    public @NotNull Set<String> getTags() {
        return Set.copyOf(tags);
    }

    @Override
    public boolean addTag(@NotNull String tag) {
        return tags.size() < 1024 && tags.add(tag);
    }

    @Override
    public boolean removeTag(@NotNull String tag) {
        return tags.remove(tag);
    }

    @Override
    public void init() {
        if (active)
            throw new IllegalStateException(this + " is already initiated");
        active = true;
        getServer().getEntityManager().addEntity(this);
    }

    @Override
    public void remove() {
        if (!active)
            throw new IllegalStateException(this + " is not active");
        active = false;
        getServer().getEntityManager().removeEntity(this);
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        NBTCompound compound = new NBTCompound(Map.ofEntries(
                entry("Pos", NBTUtils.list(location.getX(), location.getY(), location.getZ())),
                entry("Motion", NBTUtils.list(0, 0, 0)), // TODO implement motion
                entry("Rotation", NBTUtils.list(location.getYaw(), location.getPitch())),
                entry("FallDistance", fallDistance),
                entry("Fire", remainingFireTicks),
                entry("Air", (short) 0),
                entry("OnGround", (byte) (onGround ? 1 : 0)),
                entry("Invulnerable", (byte) (invulnerable ? 1 : 0)),
                entry("PortalCooldown", portalCooldown),
                entry("UUID", UUIDUtils.uuidToIntArray(uuid)),
                entry("WorldUUIDLeast", getWorld().getUuid().getLeastSignificantBits()),
                entry("WorldUUIDMost", getWorld().getUuid().getMostSignificantBits())
        ));
        if (getCustomName() != null)
            compound.set("CustomName", GsonComponentSerializer.gson().serialize(getCustomName()));
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

    @Override
    public void load(@NotNull NBTCompound nbtCompound) {
        List<Double> pos = ((List<?>) nbtCompound.get("Pos")).stream().map(o -> (double) o).collect(Collectors.toCollection(LinkedList::new));
        List<Double> motion = ((List<?>) nbtCompound.get("Motion")).stream().map(o -> (double) o).collect(Collectors.toCollection(LinkedList::new));
        List<Float> rotation = ((List<?>) nbtCompound.get("Rotation")).stream().map(o -> (float) o).collect(Collectors.toCollection(LinkedList::new));

        if (pos.size() == 0)
            pos = new LinkedList<>(List.of(0d, 0d, 0d));
        if (motion.size() == 0)
            motion = new LinkedList<>(List.of(0d, 0d, 0d));
        if (rotation.size() == 0)
            rotation = new LinkedList<>(List.of(0f, 0f));

        getLocation().setX(pos.get(0));
        getLocation().setY(pos.get(1));
        getLocation().setZ(pos.get(2));
        getLocation().setYaw(rotation.get(0));
        getLocation().setPitch(rotation.get(1));

        setFallDistance(nbtCompound.get("FallDistance", 0f));
        setRemainingFireTicks(nbtCompound.get("Fire", (short) 0));
        setOnGround(nbtCompound.get("OnGround", (byte) 0) == 1);
        setInvulnerable(nbtCompound.get("Invulnerable", (byte) 0) == 1);
        setPortalCooldown(nbtCompound.get("PortalCooldown", 0));
        if (nbtCompound.containsKey("UUID"))
            uuid = UUIDUtils.uuidFromIntArray(nbtCompound.get("UUID"));
        if (nbtCompound.containsKey("CustomName")) {
            String string = nbtCompound.get("CustomName");
            setCustomName(GsonComponentSerializer.gson().deserialize(string));
        }
        setCustomNameVisible(nbtCompound.get("CustomNameVisible", (byte) 0) == 1);
        setSilent(nbtCompound.get("Silent", (byte) 0) == 1);
        setNoGravity(nbtCompound.get("NoGravity", (byte) 0) == 1);
        setGlowing(nbtCompound.get("Glowing", (byte) 0) == 1);
        setTicksFrozen(nbtCompound.get("TicksFrozen", 0));
        setHasVisualFire(nbtCompound.get("HasVisualFire", (byte) 0) == 1);
        if (nbtCompound.containsKey("Tags")) {
            tags.clear();
            List<String> nbtStrings =  ((Collection<?>) nbtCompound.get("Tags")).stream().map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
            int i = Math.min(nbtStrings.size(), 1024);
            for (int j = 0; j < i; j++)
                tags.add(nbtStrings.get(j));
        }
    }

}
