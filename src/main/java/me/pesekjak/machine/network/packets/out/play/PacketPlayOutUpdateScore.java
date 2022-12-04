package me.pesekjak.machine.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutUpdateScore extends PacketOut {

    private static final int ID = 0x59;

    @NotNull
    private String entityName;
    @NotNull
    private Action action;
    @NotNull
    private String objectiveName;
    @Nullable
    private Integer value;

    static {
        register(PacketPlayOutUpdateScore.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateScore::new);
    }

    public PacketPlayOutUpdateScore(FriendlyByteBuf buf) {
        entityName = buf.readString(StandardCharsets.UTF_8);
        action = Action.fromID(buf.readByte());
        objectiveName = buf.readString(StandardCharsets.UTF_8);
        if (action != Action.REMOVE)
            value = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeString(entityName, StandardCharsets.UTF_8)
                .writeVarInt(action.getId())
                .writeString(objectiveName, StandardCharsets.UTF_8);
        if (action != Action.REMOVE) {
            assert value != null;
            buf.writeVarInt(value);
        }
        return buf.bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutUpdateScore(new FriendlyByteBuf(serialize()));
    }

    private enum Action {
        UPDATE,
        REMOVE;

        public int getId() {
            return ordinal();
        }

        public static @NotNull Action fromID(@Range(from = 0, to = 1) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Action type");
            return values()[id];
        }

    }
}
