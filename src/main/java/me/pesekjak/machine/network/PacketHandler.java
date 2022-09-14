package me.pesekjak.machine.network;

import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;

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
    public PacketReader read(Channel channel, PacketReader read) {
        return read;
    }

    /**
     * Called when packet is written in a Channel.
     * @param channel Channel packet is written to
     * @param write Previous writer
     * @return Next writer
     */
    public PacketWriter write(Channel channel, PacketWriter write) {
        return write;
    }

    /**
     * Called after packet is read from a Channel successfully.
     * @param channel Channel the packet has been read from
     * @param packet Packet that has been read
     */
    public void afterRead(Channel channel, PacketIn packet) {

    }

    /**
     * Called after packet is written to a Channel successfully.
     * @param channel Channel the packet has been write in
     * @param packet Packet that has been written
     */
    public void afterWrite(Channel channel, PacketOut packet) {

    }

}
