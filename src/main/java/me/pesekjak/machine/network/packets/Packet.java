package me.pesekjak.machine.network.packets;

import lombok.Getter;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public abstract class Packet implements Cloneable {

    /**
     * @return mapped ID of the packet
     */
    public abstract int getID();

    /**
     * Serializes the packet data, doesn't contain packet size and ID.
     * @return serialized packet data
     */
    public abstract byte[] serialize();

    public abstract Packet clone();

    /**
     * Serializes the full packet.
     * @return serialized packet
     */
    public byte[] rawSerialize() {
        return new FriendlyByteBuf()
                .writeVarInt(getSize())
                .writeVarInt(getID())
                .writeBytes(serialize())
                .bytes();
    }

    /**
     * Returns the size of the packet ID and packet data.
     * @return size of the packet
     */
    public int getSize() {
        return new FriendlyByteBuf()
                .writeVarInt(getID())
                .writeBytes(serialize())
                .bytes().length;
    }

    public enum PacketState {

        HANDSHAKING_IN ("000"),
        HANDSHAKING_OUT("001"),
        STATUS_IN      ("010"),
        STATUS_OUT     ("011"),
        LOGIN_IN       ("100"),
        LOGIN_OUT      ("101"),
        PLAY_IN        ("110"),
        PLAY_OUT       ("111");

        @Getter
        private final int mask;

        PacketState(String mask) {
            this.mask = Integer.parseInt(mask + "000000000000", 2);
        }

    }

}
