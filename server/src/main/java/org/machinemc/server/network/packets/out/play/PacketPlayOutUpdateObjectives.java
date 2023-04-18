package org.machinemc.server.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutUpdateObjectives extends PacketOut {

    private static final int ID = 0x56;

    private String objectiveName;
    private Action action;
    private @Nullable Component objectiveValue;
    private @Nullable DisplayType type;

    static {
        register(PacketPlayOutUpdateObjectives.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateObjectives::new);
    }

    public PacketPlayOutUpdateObjectives(final ServerBuffer buf) {
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
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        final FriendlyByteBuf buf = new FriendlyByteBuf()
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
    public PacketOut clone() {
        return new PacketPlayOutUpdateObjectives(new FriendlyByteBuf(serialize()));
    }

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE;

        public int getId() {
            return ordinal();
        }

        public static Action fromID(final @Range(from = 0, to = 2) int id) {
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

        public static DisplayType fromID(final @Range(from = 0, to = 1) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Display type");
            return values()[id];
        }
    }
}
