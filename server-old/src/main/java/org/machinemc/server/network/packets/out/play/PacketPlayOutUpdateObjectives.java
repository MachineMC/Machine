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
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutUpdateObjectives extends PacketOut {

    private static final int ID = 0x58;

    private String objectiveName;
    private Action action;
    private @Nullable ComponentProperties objectiveValue;
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
    public int getID() {
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
                .writeByte((byte) action.getID());
        if (action != Action.REMOVE) {
            assert objectiveValue != null;
            assert type != null;
            buf.writeComponent(objectiveValue)
                    .writeVarInt(type.getID());
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

        public int getID() {
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

        public int getID() {
            return ordinal();
        }

        public static DisplayType fromID(final @Range(from = 0, to = 1) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Display type");
            return values()[id];
        }
    }
}
