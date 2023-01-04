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
        Map<String, ?> map = nbtCompound.revert();
        health = map.containsKey("Health") ? (float) map.get("Health") : 0;
        hurtTime = map.containsKey("HurtTime") ? (short) map.get("HurtTime") : 0;
        hurtByTimestamp = map.containsKey("HurtByTimestamp") ? (int) map.get("HurtByTimestamp") : 0;
        deathTime = map.containsKey("DeathTime") ? (short) map.get("DeathTime") : 0;
        absorptionAmount = map.containsKey("AbsorptionAmount") ? (float) map.get("AbsorptionAmount") : 0;
        fallFlying = map.containsKey("FallFlying") && ((byte) map.get("FallFlying") == 1);
    }

}
