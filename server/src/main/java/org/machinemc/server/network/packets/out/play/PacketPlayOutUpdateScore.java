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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutUpdateScore extends PacketOut {

    private static final int ID = 0x5B;

    private String entityName;
    private Action action;
    private String objectiveName;
    private @Nullable Integer value;

    static {
        register(PacketPlayOutUpdateScore.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateScore::new);
    }

    public PacketPlayOutUpdateScore(final ServerBuffer buf) {
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
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        final FriendlyByteBuf buf = new FriendlyByteBuf()
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
    public PacketOut clone() {
        return new PacketPlayOutUpdateScore(new FriendlyByteBuf(serialize()));
    }

    private enum Action {
        UPDATE,
        REMOVE;

        public int getId() {
            return ordinal();
        }

        public static Action fromID(final @Range(from = 0, to = 1) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Action type");
            return values()[id];
        }

    }
}
