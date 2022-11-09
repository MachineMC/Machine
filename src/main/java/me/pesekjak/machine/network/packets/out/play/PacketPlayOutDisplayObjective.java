package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutDisplayObjective extends PacketOut {

    private static final int ID = 0x4F;

    // The position of the scoreboard. 0: list, 1: sidebar, 2: below name, 3 - 18: team specific sidebar, indexed as 3 + team color
    @Getter @Setter
    private byte position;
    @Getter @Setter
    private String objectiveName;

    static {
        register(PacketPlayOutDisplayObjective.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisplayObjective::new);
    }

    public PacketPlayOutDisplayObjective(FriendlyByteBuf buf) {
        position = buf.readByte();
        objectiveName = buf.readString();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(position)
                .writeString(objectiveName)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutDisplayObjective(new FriendlyByteBuf(serialize()));
    }

}
