package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.UUID;

public abstract class ServerLivingEntity extends ServerEntity implements LivingEntity {

    @Getter @Setter
    private float health;
    @Getter @Setter
    private short hurtTime;
    @Getter @Setter
    private int hurtByTimestamp;
    @Getter @Setter
    private short deathTime;
    @Getter @Setter
    private float absorptionAmount;
    @Getter @Setter
    private boolean fallFlying;

    public ServerLivingEntity(Machine server, EntityType entityType, UUID uuid) {
        super(server, entityType, uuid);
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        return super.toNBT().toMutableCompound().setFloat("Health", health)
                .setShort("HurtTime", hurtTime)
                .setInt("HurtByTimestamp", hurtByTimestamp)
                .setShort("DeathTime", deathTime)
                .setFloat("AbsorptionAmount", absorptionAmount)
                // Attributes
                // Active Effects
                .setByte("FallFlying", (byte) (fallFlying ? 1 : 0))
                // Sleeping Position
                // Brain
                .toCompound();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void load(@NotNull NBTCompound nbtCompound) {
        super.load(nbtCompound);
        health = nbtCompound.contains("Health") ? nbtCompound.getAsFloat("Health") : 0;
        hurtTime = nbtCompound.contains("HurtTime") ? nbtCompound.getAsShort("HurtTime") : 0;
        hurtByTimestamp = nbtCompound.contains("HurtByTimestamp") ? nbtCompound.getAsInt("HurtByTimestamp") : 0;
        deathTime = nbtCompound.contains("DeathTime") ? nbtCompound.getAsShort("DeathTime") : 0;
        absorptionAmount = nbtCompound.contains("AbsorptionAmount") ? nbtCompound.getAsFloat("AbsorptionAmount") : 0;
        fallFlying = nbtCompound.contains("FallFlying") ? nbtCompound.getBoolean("FallFlying") : false;
    }

}
