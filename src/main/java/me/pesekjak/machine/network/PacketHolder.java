package me.pesekjak.machine.network;

import me.pesekjak.machine.network.packets.Packet;
import org.jetbrains.annotations.Nullable;

/**
 * Object holding a server packet.
 */
public interface PacketHolder {

    @Nullable Packet getPacket();

}
