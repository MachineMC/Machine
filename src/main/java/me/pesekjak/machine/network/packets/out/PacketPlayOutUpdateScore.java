package me.pesekjak.machine.network.packets.out;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public class PacketPlayOutUpdateScore extends PacketOut {

    private static final int ID = 0x59;

    @Getter @Setter
    private String entityName;
    @Getter @Setter
    private Action action;
    @Getter @Setter
    private String objectiveName;
    @Getter @Setter @Nullable
    private Integer value;

    static {
        register(PacketPlayOutUpdateScore.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateScore::new);
    }

    public PacketPlayOutUpdateScore(FriendlyByteBuf buf) {
        entityName = buf.readString();
        action = Action.fromID(buf.readByte());
        objectiveName = buf.readString();
        if (action != Action.REMOVE)
            value = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeString(entityName)
                .writeVarInt(action.getId())
                .writeString(objectiveName);
        if (action != Action.REMOVE) {
            assert value != null;
            buf.writeVarInt(value);
        }
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
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
