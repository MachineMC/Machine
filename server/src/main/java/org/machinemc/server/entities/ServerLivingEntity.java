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
