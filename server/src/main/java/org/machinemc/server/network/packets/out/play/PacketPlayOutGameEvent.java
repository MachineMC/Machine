package org.machinemc.server.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Range;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutGameEvent extends PacketOut {

    private static final int ID = 0x1D;

    private Event event;
    private float value;

    static {
        register(PacketPlayOutGameEvent.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutGameEvent::new);
    }

    public PacketPlayOutGameEvent(final ServerBuffer buf) {
        event = Event.fromID(buf.readByte());
        value = buf.readFloat();
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

        /**
         * @return id of the event
         */
        public byte getId() {
            return (byte) ordinal();
        }

        /**
         * Returns event with given id.
         * @param id id
         * @return event
         */
        public static Event fromID(final @Range(from = 0, to = 11) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Event type");
            return values()[id];
        }

    }

}
