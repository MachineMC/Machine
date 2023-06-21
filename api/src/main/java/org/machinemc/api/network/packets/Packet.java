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
package org.machinemc.api.network.packets;

import lombok.Getter;
import org.jetbrains.annotations.*;

import java.util.Set;

/**
 * Represents a server packet.
 */
public interface Packet extends Cloneable {

    /**
     * @return mapped id of the packet
     */
    int getID();

    /**
     * @return packet state used by the packet
     */
    PacketState getPacketState();

    /**
     * Serializes the packet data, doesn't contain packet size and id.
     * @return serialized packet data
     */
    byte[] serialize();

    /**
     * Serializes the full packet including size and id.
     * @return serialized packet
     */
    byte[] rawSerialize();

    /**
     * @return clone of this packet
     */
    Packet clone();

    /**
     * Represents different groups of packets.
     */
    enum PacketState {

        HANDSHAKING_IN(0b000),
        HANDSHAKING_OUT(0b001),
        STATUS_IN(0b010),
        STATUS_OUT(0b011),
        LOGIN_IN(0b100),
        LOGIN_OUT(0b101),
        PLAY_IN(0b110),
        PLAY_OUT(0b111);

        public static final int OFFSET = 12;

        @Getter
        private final int mask;

        PacketState(final int mask) {
            this.mask = mask << OFFSET;
        }

        /**
         * Returns a packet state from its mask.
         * @param mask mask of the packet state
         * @return packet state with given mask
         */
        public static @Nullable PacketState fromMask(final @Range(from = 0, to = 0b111) int mask) {
            for (final PacketState state : values()) {
                if (state.mask == mask) return state;
            }
            return null;
        }

        /**
         * @return unmodifiable set of packet states for packets from client to server
         */
        public static @Unmodifiable Set<PacketState> in() {
            return Set.of(HANDSHAKING_IN, STATUS_IN, LOGIN_IN, PLAY_IN);
        }

        /**
         * @return unmodifiable set of packet states for packets from server to client
         */
        public static @Unmodifiable Set<PacketState> out() {
            return Set.of(HANDSHAKING_OUT, STATUS_OUT, LOGIN_OUT, PLAY_OUT);
        }

    }

}
