package org.machinemc.server.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutEntityAnimation extends PacketOut {

    private static final int ID = 0x03;

    private int entityId;
    private @NotNull Animation animation;

    static {
        register(PacketPlayOutEntityAnimation.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityAnimation::new);
    }

    public PacketPlayOutEntityAnimation(@NotNull ServerBuffer buf) {
        entityId = buf.readVarInt();
        animation = Animation.fromID(buf.readByte());
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
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeByte((byte) animation.getId())
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutEntityAnimation(new FriendlyByteBuf(serialize()));
    }

    public enum Animation {
        SWING_MAIN_ARM,
        TAKE_DAMAGE,
        LEAVE_BED,
        SWING_OFFHAND,
        CRITICAL_EFFECT,
        MAGIC_CRITICAL_EFFECT;

        public int getId() {
            return ordinal();
        }

        public static @NotNull Animation fromID(@Range(from = 0, to = 5) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Animation type");
            return values()[id];
        }

    }
}
