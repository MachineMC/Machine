package org.machinemc.server.network;

import org.machinemc.api.network.packets.Packet;
import org.jetbrains.annotations.Nullable;

/**
 * Object holding a server packet.
 */
public interface PacketHolder {

    @Nullable Packet getPacket();

}
