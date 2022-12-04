package me.pesekjak.machine.network.packets.in.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketStatusInRequest extends PacketIn {

    private static final int ID = 0x00;

    static {
        register(PacketStatusInRequest.class, ID, PacketState.STATUS_IN,
                PacketStatusInRequest::new
        );
    }

    public PacketStatusInRequest(FriendlyByteBuf buf) {

    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.STATUS_IN;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new byte[1];
    }

    @Override
    public @NotNull PacketIn clone() {
        return new PacketStatusInRequest(new FriendlyByteBuf(serialize()));
    }

}
