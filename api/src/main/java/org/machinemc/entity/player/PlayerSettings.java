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
package org.machinemc.entity.player;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.With;
import org.machinemc.chat.ChatMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a client settings for multiplayer servers.
 *
 * @param locale language selected by the player
 * @param viewDistance view distance of the player
 * @param chatMode chat mode selected by the player
 * @param chatColors whether the player enabled chat colors
 * @param skinParts skin parts displayed on the player model
 * @param mainHand main hand of the player
 * @param textFiltering whether the text on signs and written book titles should be filtered, currently
 *                      always false on Notchian client
 * @param allowServerListing server lists online players, this option disables this for the player
 */
@With
public record PlayerSettings(String locale,
                             byte viewDistance,
                             ChatMode chatMode,
                             boolean chatColors,
                             @With(AccessLevel.NONE) Set<SkinPart> skinParts,
                             MainHand mainHand,
                             boolean textFiltering,
                             boolean allowServerListing) {

    public PlayerSettings {
        Preconditions.checkNotNull(locale, "Locale can not be null");
        Preconditions.checkNotNull(chatMode, "Chat mode can not be null");
        Preconditions.checkNotNull(skinParts, "Skin parts can not be null");
        Preconditions.checkNotNull(mainHand, "Main hand can not be null");
        Preconditions.checkArgument(locale.length() <= 16, "Locale cannot be longer than 16 characters. Given locale: " + locale);
        skinParts = Collections.unmodifiableSet(skinParts);
    }

    /**
     * Returns copy of this player settings with different active
     * skin parts.
     *
     * @param skinParts new active skin parts
     * @return new player settings
     */
    public PlayerSettings withSkinParts(final SkinPart... skinParts) {
        return withSkinParts(EnumSet.copyOf(Arrays.asList(skinParts)));
    }

    /**
     * Returns copy of this player settings with different active
     * skin parts.
     *
     * @param skinParts new active skin parts
     * @return new player settings
     */
    // We define this method manually rather than using Lombok,
    // otherwise it won't compile
    public PlayerSettings withSkinParts(final Set<SkinPart> skinParts) {
        return new PlayerSettings(
                locale,
                viewDistance,
                chatMode,
                chatColors,
                skinParts,
                mainHand,
                textFiltering,
                allowServerListing
        );
    }

}
