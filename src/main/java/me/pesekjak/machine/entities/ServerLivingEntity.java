package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import mx.kenzie.nbt.NBT;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Default living entity implementation.
 */
@Getter @Setter
public abstract class ServerLivingEntity extends ServerEntity implements LivingEntity {

    private float health;
    private short hurtTime;
    private int hurtByTimestamp;
    private short deathTime;
    private float absorptionAmount;
    private boolean fallFlying;

    public ServerLivingEntity(Machine server, EntityType entityType, UUID uuid) {
        super(server, entityType, uuid);
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        NBTCompound compound = super.toNBT();
        compound.putAll(Map.of(
                "Health", NBT.convert(health),
                "HurtTime", NBT.convert(hurtTime),
                "HurtByTimestamp", NBT.convert(hurtByTimestamp),
                "DeathTime", NBT.convert(deathTime),
                "AbsorptionAmount", NBT.convert(absorptionAmount),
                // Attributes
                // Active Effects
                "FallFlying", NBT.convert((byte) (fallFlying ? 1 : 0))
                // Sleeping Position
                // Brain
        ));
        return compound;
    }

    @Override
    public void load(@NotNull NBTCompound nbtCompound) {
        super.load(nbtCompound);
        setHealth(nbtCompound.get("Health", 0f));
        setHurtTime(nbtCompound.get("HurtTime", (short) 0));
        setHurtByTimestamp(nbtCompound.get("HurtByTimestamp", 0));
        setDeathTime(nbtCompound.get("DeathTime", (short) 0));
        setAbsorptionAmount(nbtCompound.get("AbsorptionAmount", 0f));
        setFallFlying(nbtCompound.get("FallFlying", (byte) 0) == 1);
    }

}
