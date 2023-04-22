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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.entities;

/**
 * Represents a living entity, such as a monster or player.
 */
public interface LivingEntity extends Entity {

    /**
     * @return health of the entity
     */
    float getHealth();

    /**
     * @param health new health
     */
    void setHealth(float health);

    /**
     * @return hurt time of the entity
     */
    short getHurtTime();

    /**
     * @param hurtTime new hurt time
     */
    void setHurtTime(short hurtTime);

    /**
     * @return hurt time by timestamp
     */
    int getHurtByTimestamp();

    /**
     * @param hurtByTimestamp new hurt time by timestamp
     */
    void setHurtByTimestamp(int hurtByTimestamp);

    /**
     * @return death time of the entity
     */
    short getDeathTime();

    /**
     * @param deathTime new death time
     */
    void setDeathTime(short deathTime);

    /**
     * @return absorption amount of the entity
     */
    float getAbsorptionAmount();

    /**
     * @param absorptionAmount new absorption amount
     */
    void setAbsorptionAmount(float absorptionAmount);

    /**
     * @return if the entity is fall flying
     */
    boolean isFallFlying();

    /**
     * @param fallFlying new fall flying
     */
    void setFallFlying(boolean fallFlying);

}
