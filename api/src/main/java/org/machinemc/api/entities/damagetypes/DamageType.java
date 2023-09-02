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
package org.machinemc.api.entities.damagetypes;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;

public interface DamageType extends NBTSerializable {

    /**
     * @return namespaced key of the damage type
     */
    NamespacedKey getName();


    /**
     * @return the amount of hunger exhaustion caused by this damage type
     */
    float getExhaustion();

    /**
     * @return effects controlling how incoming damage is shown to the player
     */
    Effects getEffects();

    /**
     * @return whether this damage type scales with difficulty
     */
    Scaling getScaling();

    /**
     * @return message ID of the damage type
     */
    String getMessageID();

    /**
     * @return death message type of the damage type
     */
    @Nullable DeathMessageType getDeathMessageType();

    enum Scaling {

        NEVER,
        WHEN_CAUSED_BY_LIVING_NON_PLAYER,
        ALWAYS

    }

    enum Effects {

        HURT,
        THORNS,
        DROWNING,
        BURNING,
        POKING,
        FREEZING

    }

    enum DeathMessageType {

        DEFAULT,
        FALL_VARIANTS,
        INTENTIONAL_GAME_DESIGN

    }

}
