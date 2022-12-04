package me.pesekjak.machine.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutUpdateObjectives extends PacketOut {

    private static final int ID = 0x56;

    @NotNull
    private String objectiveName;
    @NotNull
    private Action action;
    @Nullable
    private Component objectiveValue;
    @Nullable
    private DisplayType type;

    static {
        register(PacketPlayOutUpdateObjectives.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateObjectives::new);
    }

    public PacketPlayOutUpdateObjectives(FriendlyByteBuf buf) {
        objectiveName = buf.readString(StandardCharsets.UTF_8);
        action = Action.fromID(buf.readByte());
        if (action != Action.REMOVE) {
            objectiveValue = buf.readComponent();
            type = DisplayType.fromID(buf.readVarInt());
        }
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
                .writeString(objectiveName, StandardCharsets.UTF_8)
                .writeByte((byte) action.getId());
        if (action != Action.REMOVE) {
            assert objectiveValue != null;
            assert type != null;
            buf.writeComponent(objectiveValue)
                    .writeVarInt(type.getId());
        }
        return buf.bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutUpdateObjectives(new FriendlyByteBuf(serialize()));
    }

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE;

        public int getId() {
            return ordinal();
        }

        public static @NotNull Action fromID(@Range(from = 0, to = 2) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Action type");
            return values()[id];
        }

    }

    private enum DisplayType {
        INTEGER,
        HEARTS;

        public int getId() {
            return ordinal();
        }

        public static @NotNull DisplayType fromID(@Range(from = 0, to = 1) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Display type");
            return values()[id];
        }
    }
}
