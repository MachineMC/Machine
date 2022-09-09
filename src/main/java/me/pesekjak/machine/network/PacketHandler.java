package me.pesekjak.machine.network;

/**
 * Reads and writes from and to the {@link Channel}, can be used
 * to modify the stream of packets.
 */
public class PacketHandler {

    /**
     * Called when channel reads the packet.
     * @param read Previous reader
     * @return Next reader
     */
    public PacketReader read(PacketReader read) {
        return read;
    }

    /**
     * Called when channel writes the packet.
     * @param write Previous writer
     * @return Next writer
     */
    public PacketWriter write(PacketWriter write) {
        return write;
    }

}
