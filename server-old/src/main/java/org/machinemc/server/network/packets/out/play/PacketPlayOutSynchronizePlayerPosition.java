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
package org.machinemc.server.network.packets.out.play;

import lombok.*;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.EntityPosition;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutSynchronizePlayerPosition  extends PacketOut {

    private static final int ID = 0x3C;

    static {
        register(PacketPlayOutSynchronizePlayerPosition.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSynchronizePlayerPosition::new);
    }

    private double x, y, z;
    private float yaw, pitch;
    private Set<TeleportFlags> flags;
    private int teleportID;

    public PacketPlayOutSynchronizePlayerPosition(final ServerBuffer buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        flags = TeleportFlags.unpack(buf.readByte());
        teleportID = buf.readVarInt();
    }

    public PacketPlayOutSynchronizePlayerPosition(final EntityPosition position,
                                                  final Set<TeleportFlags> flags,
                                                  final int teleportID) {
        this(position.getX(), position.getY(), position.getZ(), position.getYaw(),
                position.getPitch(), flags, teleportID);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(x)
                .writeDouble(y)
                .writeDouble(z)
                .writeFloat(yaw)
                .writeFloat(pitch)
                .writeByte((byte) TeleportFlags.pack(flags))
                .writeVarInt(teleportID)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSynchronizePlayerPosition(new FriendlyByteBuf(serialize()));
    }

    public enum TeleportFlags {
        X,
        Y,
        Z,
        PITCH,
        YAW;

        private int getMask() {
            return 1 << ordinal();
        }

        private boolean isSet(final int flag) {
            return (flag & getMask()) == getMask();
        }

        /**
         * Creates set of teleport flags from a bit mask.
         * @param flag bit mask
         * @return teleport flags
         */
        public static @Unmodifiable Set<TeleportFlags> unpack(final int flag) {
            final Set<TeleportFlags> set = EnumSet.noneOf(TeleportFlags.class);
            for (final TeleportFlags value : values()) {
                if (value.isSet(flag))
                    set.add(value);
            }
            return Collections.unmodifiableSet(set);
        }

        /**
         * Creates mask from given flags.
         * @param flags flags
         * @return created bit mask
         */
        public static int pack(final Set<TeleportFlags> flags) {
            int flag = 0;

            for (final TeleportFlags teleportFlag : flags)
                flag |= teleportFlag.getMask();

            return flag;
        }

    }

}
