/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Range;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutGameEvent extends PacketOut {

    private static final int ID = 0x1F;

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
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(event.getID())
                .writeFloat(value)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutGameEvent(new FriendlyByteBuf(serialize()));
    }

    public enum Event {
        NO_RESPAWN_BLOCK_AVAILABLE,
        BEGIN_RAINING,
        END_RAINING,
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
        public byte getID() {
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
