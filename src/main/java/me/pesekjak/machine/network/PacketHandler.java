package me.pesekjak.machine.network;

import me.pesekjak.machine.network.packets.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * Reads and writes from and to the {@link Channel}, can be used
 * to modify the stream of packets.
 */
public class PacketHandler {

    /**
     * Called when packet is read from a Channel.
     * @param channel Channel packet is read from
     * @param read Previous reader
     * @return Next reader
     */
    public @NotNull PacketReader read(@NotNull Channel channel, @NotNull PacketReader read) {
        return read;
    }

    /**
     * Called when packet is written in a Channel.
     * @param channel Channel packet is written to
     * @param write Previous writer
     * @return Next writer
     */
    public @NotNull PacketWriter write(@NotNull Channel channel, @NotNull PacketWriter write) {
        return write;
    }

    /**
     * Called after packet is read from a Channel successfully.
     * @param channel Channel the packet has been read from
     * @param packet Packet that has been read
     */
    public void afterRead(@NotNull Channel channel, @NotNull Packet packet) {

    }

    /**
     * Called after packet is written to a Channel successfully.
     * @param channel Channel the packet has been write in
     * @param packet Packet that has been written
     */
    public void afterWrite(@NotNull Channel channel, @NotNull Packet packet) {

    }

}
