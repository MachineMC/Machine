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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.entities.player.Hand;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.api.entities.player.SkinPart;
import org.machinemc.scriptive.components.Component;

import java.util.Set;

/**
 * Represents a human entity, such as an NPC or a player.
 */
public interface HumanEntity extends LivingEntity {

    /**
     * @return profile of the human
     */
    PlayerProfile getProfile();

    /**
     * @return username of the profile of the human
     */
    default String getUsername() {
        return getProfile().getUsername();
    }

    /**
     * @return gamemode of the human
     */
    Gamemode getGamemode();

    /**
     * Changes gamemode of the human.
     * @param gamemode new gamemode
     */
    void setGamemode(Gamemode gamemode);

    /**
     * @return unmodifiable set of enabled skin parts of the human's skin
     */
    @Unmodifiable Set<SkinPart> getDisplayedSkinParts();

    /**
     * @return main hand of the human
     */
    Hand getMainHand();

    /**
     * @return display name of the human
     */
    Component getDisplayName();

    /**
     * Changes the display name of the human.
     * @param displayName new display name
     */
    void setDisplayName(@Nullable Component displayName);

    /**
     * @return name of the human in the player list
     */
    Component getPlayerListName();

    /**
     * Changes the player list name of the human.
     * @param playerListName new player list name
     */
    void setPlayerListName(@Nullable Component playerListName);

}
