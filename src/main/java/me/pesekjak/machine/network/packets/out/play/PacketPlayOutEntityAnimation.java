package me.pesekjak.machine.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public class PacketPlayOutEntityAnimation extends PacketOut {

    private static final int ID = 0x03;

    @Getter
    private int entityId;
    @Getter @Setter
    private Animation animation;

    static {
        register(PacketPlayOutEntityAnimation.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutEntityAnimation::new);
    }

    public PacketPlayOutEntityAnimation(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        animation = Animation.fromID(buf.readByte());
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeByte((byte) animation.getId())
                .bytes();
    }

    @Override
    public PacketOut clone() {
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
