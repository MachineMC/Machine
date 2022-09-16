package me.pesekjak.machine.network.packets.out;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.Range;

public class PacketPlayOutGameEvent extends PacketOut {

    private static final int ID = 0x1D;

    @Getter @Setter
    private Event event;
    @Getter @Setter
    private float value;

    static {
        PacketOut.register(PacketPlayOutGameEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutGameEvent::new);
    }

    public PacketPlayOutGameEvent(FriendlyByteBuf buf) {
        event = Event.fromID(buf.readByte());
        value = buf.readFloat();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(event.getId())
                .writeFloat(value)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutGameEvent(new FriendlyByteBuf(serialize()));
    }

    public enum Event {
        NO_RESPAWN_BLOCK_AVAILABLE,
        END_RAINING,
        BEGIN_RAINING,
        CHANGE_GAMEMODE,
        WIN_GAME,
        DEMO_EVENT,
        ARROW_HIT_PLAYER,
        RAID_LEVEL_CHANGE,
        THUNDER_LEVEL_CHANGE,
        PLAY_PUFFERFISH_STING_SOUND,
        PLAY_ELDER_GUARDIAN_APPEARANCE,
        ENABLE_RESPAWN_SCREEN;

        public byte getId() {
            return (byte) ordinal();
        }

        public static Event fromID(@Range(from = 0, to = 11) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Event type");
            return values()[id];
        }

    }

}
