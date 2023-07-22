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

import lombok.*;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.EntityPosition;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutSpawnPlayer extends PacketOut {

    private static final int ID = 0x03;

    private int entityID;
    @Getter(AccessLevel.NONE)
    private UUID uuid;
    private EntityPosition position;


    static {
        register(PacketPlayOutSpawnPlayer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnPlayer::new);
    }

    public PacketPlayOutSpawnPlayer(final ServerBuffer buf) {
        entityID = buf.readVarInt();
        uuid = buf.readUUID();
        position = EntityPosition.read(buf);
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
                .writeVarInt(entityID)
                .writeUUID(uuid)
                .write(position)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSpawnPlayer(new FriendlyByteBuf(serialize()));
    }

    /**
     * @return uuid of the player
     */
    public UUID getUUID() {
        return uuid;
    }

}
