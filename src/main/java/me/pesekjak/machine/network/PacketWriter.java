package me.pesekjak.machine.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.Packet;
import org.jetbrains.annotations.Nullable;

/**
 * Holder of a packet that will be written to
 * a client connection later.
 */
@AllArgsConstructor
public class PacketWriter implements PacketHolder {

    @Getter @Setter
    private @Nullable Packet packet;

}
