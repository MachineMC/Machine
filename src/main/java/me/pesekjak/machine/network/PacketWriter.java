package me.pesekjak.machine.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class PacketWriter implements PacketHolder {

    @Getter @Setter @Nullable
    private PacketOut packet;

}
