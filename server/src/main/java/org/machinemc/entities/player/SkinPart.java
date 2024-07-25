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
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public enum SkinPart {
    
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40);

    private final byte bitMask;

    SkinPart(final int bitMask) {
        this.bitMask = (byte) bitMask;
    }

    public byte bitMask() {
        return bitMask;
    }

    public Optional<SkinPart> getByName(@Nullable String name) {
        if (name == null) return Optional.empty();
        for (final SkinPart part : values()) {
            if (part.name().equalsIgnoreCase(name))
                return Optional.of(part);
        }
        return Optional.empty();
    }

    public static int skinMask(final SkinPart... parts) {
        Preconditions.checkNotNull(parts, "Skin parts can not be null");
        int mask = 0;
        for (SkinPart part : parts) {
            Preconditions.checkNotNull(parts, "Skin part can not be null");
            mask |= part.bitMask();
        }
        return mask;
    }

    public static int skinMask(final Set<SkinPart> parts) {
        Preconditions.checkNotNull(parts, "Skin parts can not be null");
        int mask = 0;
        for (SkinPart part : parts) {
            Preconditions.checkNotNull(parts, "Skin part can not be null");
            mask |= part.bitMask();
        }
        return mask;
    }

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
