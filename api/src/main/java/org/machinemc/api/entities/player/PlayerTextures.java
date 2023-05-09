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
package org.machinemc.api.entities.player;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * Represents player's skin textures.
 */
public interface PlayerTextures extends Writable {

    /**
     * @return base64 texture value of the skin
     */
    String value();

    /**
     * @return signature of the skin
     */
    @Nullable String signature();

    /**
     * @return URL of the skin texture
     */
    URL skinUrl();

    /**
     * @return URL of the texture of skin's cape
     */
    @Nullable URL capeUrl();

    /**
     * @return type of the skin model
     */
    SkinModel skinModel();

    /**
     * Writes the player textures into a buffer.
     * @param buf buffer to write into
     */
    default void write(ServerBuffer buf) {
        buf.writeTextures(this);
    }

}
