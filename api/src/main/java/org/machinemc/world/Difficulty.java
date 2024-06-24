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
package org.machinemc.world;

import lombok.Getter;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;

/**
 * Represents a game difficulty of a world.
 * <p>
 * By default, Machine does not offer any special behaviour for
 * this feature, but it can be extended by 3rd party plugins.
 * <p>
 * This documentation explains the vanilla behaviour of difficulty feature.
 */
@Getter
public enum Difficulty {

    /**
     * Players regain health over time, hostile mobs don't spawn,
     * the hunger bar does not deplete.
     */
    PEACEFUL("options.difficulty.peaceful"),

    /**
     *
     Hostile mobs spawn, enemies deal less damage than on normal difficulty,
     the hunger bar does deplete and starving deals up to 5 hearts of damage.
     */
    EASY("options.difficulty.easy"),

    /**
     * Hostile mobs spawn, enemies deal normal amounts of damage,
     * the hunger bar does deplete and starving deals up to 9.5 hearts of damage.
     */
    NORMAL("options.difficulty.normal"),

    /**
     * Hostile mobs spawn, enemies deal greater damage than on normal difficulty,
     * the hunger bar does deplete and starving can kill players.
     */
    HARD("options.difficulty.hard");

    /**
     * Display name of the difficulty.
     */
    private final Component displayName;

    Difficulty(String translation) {
        displayName = TranslationComponent.of(translation);
    }

}
