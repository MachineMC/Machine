package org.machinemc.server.entities;

import lombok.Getter;
import lombok.Setter;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.EntityType;
import org.machinemc.api.entities.LivingEntity;

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

    public ServerLivingEntity(final Machine server, final EntityType entityType, final UUID uuid) {
        super(server, entityType, uuid);
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = super.toNBT();
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
    public void load(final NBTCompound nbtCompound) {
        super.load(nbtCompound);
        setHealth(nbtCompound.getValue("Health", 0f));
        setHurtTime(nbtCompound.getValue("HurtTime", (short) 0));
        setHurtByTimestamp(nbtCompound.getValue("HurtByTimestamp", 0));
        setDeathTime(nbtCompound.getValue("DeathTime", (short) 0));
        setAbsorptionAmount(nbtCompound.getValue("AbsorptionAmount", 0f));
        setFallFlying(nbtCompound.getValue("FallFlying", (byte) 0) == 1);
    }

}
