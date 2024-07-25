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
package org.machinemc.entities.player;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.With;
import org.machinemc.chat.ChatMode;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@With
public record PlayerSettings(
        String locale,
        byte viewDistance,
        ChatMode chatMode,
        boolean chatColors,
        @With(AccessLevel.NONE) Set<SkinPart> skinParts,
        MainHand mainHand,
        boolean textFiltering,
        boolean allowServerListing
) {

    public PlayerSettings {
        Preconditions.checkArgument(locale.length() <= 16, "Locale cannot be longer than 16 characters. Given locale: " + locale);
    }

    public PlayerSettings withSkinParts(final SkinPart... skinParts) {
        return withSkinParts(EnumSet.copyOf(Arrays.asList(skinParts)));
    }

    // For some reason we have to define this method manually rather than using Lombok,
    //  otherwise it won't compile?
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
