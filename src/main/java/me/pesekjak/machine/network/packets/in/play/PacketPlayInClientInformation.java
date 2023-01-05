package me.pesekjak.machine.network.packets.in.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.chat.ChatMode;
import me.pesekjak.machine.entities.player.Hand;
import me.pesekjak.machine.entities.player.SkinPart;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayInClientInformation extends PacketIn {

    private static final int ID = 0x08;

    private @NotNull String locale;
    private byte viewDistance;
    private @NotNull ChatMode chatMode;
    private boolean chatColor;
    private @NotNull Set<SkinPart> displayedSkinParts;
    private @NotNull Hand mainHand;
    private boolean enableTextFiltering;
    private boolean allowServerListings;

    static {
        register(PacketPlayInClientInformation.class, ID, PacketState.PLAY_IN,
                PacketPlayInClientInformation::new);
    }

    public PacketPlayInClientInformation(@NotNull ServerBuffer buf) {
        locale = buf.readString(StandardCharsets.UTF_8);
        viewDistance = buf.readByte();
        chatMode = ChatMode.fromID(buf.readVarInt());
        chatColor = buf.readBoolean();
        displayedSkinParts = SkinPart.fromMask(buf.readByte());
        mainHand = Hand.fromID(buf.readVarInt());
        enableTextFiltering = buf.readBoolean();
        allowServerListings = buf.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_IN;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeString(locale, StandardCharsets.UTF_8)
                .writeByte(viewDistance)
                .writeVarInt(chatMode.getID())
                .writeBoolean(chatColor)
                .writeByte((byte) SkinPart.skinMask(displayedSkinParts.toArray(new SkinPart[0])))
                .writeVarInt(mainHand.getId())
                .writeBoolean(enableTextFiltering)
                .writeBoolean(allowServerListings)
                .bytes();
    }

    @Override
    public @NotNull PacketIn clone() {
        return new PacketPlayInClientInformation(new FriendlyByteBuf(serialize()));
    }

}
