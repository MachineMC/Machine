package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.EntityUtils;
import me.pesekjak.machine.utils.NBTUtils;
import me.pesekjak.machine.utils.UUIDUtils;
import me.pesekjak.machine.world.Location;
import me.pesekjak.machine.world.World;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        return NBT.Compound(nbt -> {
            nbt.set("Pos", NBTUtils.doubleList(location.getX(), location.getY(), location.getZ()));
            nbt.set("Motion", NBTUtils.doubleList(0, 0, 0)); // TODO implement motion
            nbt.set("Rotation", NBTUtils.floatList(location.getYaw(), location.getPitch()));
            nbt.setFloat("FallDistance", fallDistance);
            nbt.setShort("Fire", remainingFireTicks);
            nbt.setShort("Air", (short) 0);
            nbt.setByte("OnGround", (byte) (onGround ? 1 : 0));
            nbt.setByte("Invulnerable", (byte) (invulnerable ? 1 : 0));
            nbt.setInt("PortalCooldown", portalCooldown);
            nbt.setIntArray("UUID", UUIDUtils.uuidToIntArray(uuid));
            nbt.setLong("WorldUUIDLeast", getWorld().getUuid().getLeastSignificantBits());
            nbt.setLong("WorldUUIDMost", getWorld().getUuid().getMostSignificantBits());

            if (getCustomName() != null)
                nbt.setString("CustomName", GsonComponentSerializer.gson().serialize(getCustomName()));

            if (isCustomNameVisible())
                nbt.setByte("CustomNameVisible", (byte) (isCustomNameVisible() ? 1 : 0));

            if (silent)
                nbt.setByte("Silent", (byte) 1);

            if (noGravity)
                nbt.setByte("NoGravity", (byte) 1);

            if (glowing)
                nbt.setByte("Glowing", (byte) 1);

            if (ticksFrozen > 0)
                nbt.setInt("TicksFrozen", (byte) ticksFrozen);

            if (hasVisualFire)
                nbt.setByte("HasVisualFire", (byte) 1);

            if (!tags.isEmpty())
                nbt.set("Tags", new NBTList<>(NBTType.TAG_String, tags.stream()
                        .map(NBTString::new)
                        .toList()));
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void load(@NotNull NBTCompound nbtCompound) {
        NBTList<NBTDouble> pos = nbtCompound.getList("Pos");
        NBTList<NBTDouble> motion = nbtCompound.getList("Motion");
        NBTList<NBTFloat> rotation = nbtCompound.getList("Rotation");

        if (pos == null)
            pos = NBTUtils.doubleList(0, 0, 0);
        if (motion == null)
            motion = NBTUtils.doubleList(0, 0, 0);
        if (rotation == null)
            rotation = NBTUtils.floatList(0, 0);

        getLocation().setX(pos.get(0).getValue());
        getLocation().setY(pos.get(1).getValue());
        getLocation().setZ(pos.get(2).getValue());
        getLocation().setYaw(rotation.get(0).getValue());
        getLocation().setPitch(rotation.get(1).getValue());

        fallDistance = nbtCompound.contains("FallDistance") ? nbtCompound.getAsFloat("FallDistance") : 0;
        remainingFireTicks = nbtCompound.contains("Fire") ? nbtCompound.getAsShort("Fire") : 0;
        onGround = nbtCompound.contains("OnGround") ? nbtCompound.getBoolean("OnGround") : false;
        invulnerable = nbtCompound.contains("Invulnerable") ? nbtCompound.getBoolean("Invulnerable") : false;
        portalCooldown = nbtCompound.contains("PortalCooldown") ? nbtCompound.getAsInt("PortalCooldown") : 0;
        if (nbtCompound.contains("UUID"))
            uuid = UUIDUtils.uuidFromIntArray(nbtCompound.getIntArray("UUID").copyArray());
        if (nbtCompound.contains("CustomName")) {
            String string = nbtCompound.getString("CustomName");
            setCustomName(GsonComponentSerializer.gson().deserialize(string));
        }
        setCustomNameVisible(nbtCompound.contains("CustomNameVisible") ? nbtCompound.getBoolean("CustomNameVisible") : false);
        silent = nbtCompound.contains("Silent") ? nbtCompound.getBoolean("Silent") : false;
        noGravity = nbtCompound.contains("NoGravity") ? nbtCompound.getBoolean("NoGravity") : false;
        glowing = nbtCompound.contains("Glowing") ? nbtCompound.getBoolean("Glowing") : false;
        ticksFrozen = nbtCompound.contains("TicksFrozen") ? nbtCompound.getInt("TicksFrozen") : 0;
        hasVisualFire = (nbtCompound.contains("HasVisualFire") ? nbtCompound.getBoolean("HasVisualFire") : false);
        if (nbtCompound.contains("Tags")) {
            tags.clear();
            NBTList<NBTString> nbtStrings = nbtCompound.getList("Tags");
            int i = Math.min(nbtStrings.getSize(), 1024);
            for (int j = 0; j < i; j++)
                tags.add(nbtStrings.get(j).getValue());
        }
    }

}
