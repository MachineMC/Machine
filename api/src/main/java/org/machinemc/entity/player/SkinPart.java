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

import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a part of a player skin (player model)
 * that can be turned on or off in the client settings.
 */
public enum SkinPart {

    /**
     * Cape.
     */
    CAPE(0x01),

    /**
     * Jacket.
     */
    JACKET(0x02),

    /**
     * Left sleeve.
     */
    LEFT_SLEEVE(0x04),

    /**
     * Right sleeve.
     */
    RIGHT_SLEEVE(0x08),

    /**
     * Left pants.
     */
    LEFT_PANTS(0x10),

    /**
     * Right pants.
     */
    RIGHT_PANTS(0x20),

    /**
     * Hat.
     */
    HAT(0x40);

    private final byte bitMask;

    SkinPart(final int bitMask) {
        this.bitMask = (byte) bitMask;
    }

    /**
     * Returns bit mask of the skin part.
     * <p>
     * This value is used by Minecraft protocol.
     *
     * @return skin part bit mask
     */
    public byte bitMask() {
        return bitMask;
    }

    /**
     * Creates a skin mask from a multiple skin parts.
     * <p>
     * This value is used by Minecraft protocol.
     *
     * @param parts skin parts
     * @return skin mask
     */
    public static int skinMask(final SkinPart... parts) {
        Preconditions.checkNotNull(parts, "Skin parts can not be null");
        int mask = 0;
        for (SkinPart part : parts) {
            Preconditions.checkNotNull(parts, "Skin part can not be null");
            mask |= part.bitMask();
        }
        return mask;
    }

    /**
     * Creates a skin mask from a multiple skin parts.
     * <p>
     * This value is used by Minecraft protocol.
     *
     * @param parts skin parts
     * @return skin mask
     */
    public static int skinMask(final Set<SkinPart> parts) {
        Preconditions.checkNotNull(parts, "Skin parts can not be null");
        int mask = 0;
        for (SkinPart part : parts) {
            Preconditions.checkNotNull(parts, "Skin part can not be null");
            mask |= part.bitMask();
        }
        return mask;
    }

    /**
     * Converts skin mask back to a collection of skin parts.
     *
     * @param mask skin mask
     * @return skin parts
     */
    public static Set<SkinPart> fromMask(int mask) {
        final Set<SkinPart> set = EnumSet.noneOf(SkinPart.class);
        int index = 0;
        final SkinPart[] values = values();
        final int length = values.length;
        while (mask != 0 || index >= length) {
            if ((mask & 1) == 1)
                set.add(values[index++]);
            mask >>>= 1;
        }
        return set;
    }

}
