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
package org.machinemc.api.server.codec;

import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;

import java.util.List;
import java.util.Map;

/**
 * Part of Minecraft's codec.
 */
public interface CodecPart {

    /**
     * @return String identifier of the codec part
     */
    String getCodecType();

    /**
     * @return NBT values of the codec
     */
    List<NBTCompound> getCodecElements();

    /**
     * Builds the full NBTCompound of the codec type and its elements.
     * @return full NBTCompound of the codec part
     */
    default NBTCompound getCodecNBT() {
        return new NBTCompound(Map.of(
                "type", getCodecType(),
                "value", new NBTList(getCodecElements())
        ));
    }

}
