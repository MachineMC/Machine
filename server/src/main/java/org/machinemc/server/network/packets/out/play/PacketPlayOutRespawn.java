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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.server.network.packets.PacketOut;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutRespawn extends PacketOut {

    private static final int ID = 0x3E;

    private NamespacedKey worldType, worldName;
    private long hashedSeed;
    private Gamemode gamemode;
    private @Nullable Gamemode previousGamemode;
    private boolean isDebug, isFlat, copyMetadata, hasDeathLocation;
    private @Nullable NamespacedKey deathWorldName;
    private @Nullable BlockPosition deathLocation;

    static {
        register(PacketPlayOutRespawn.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRespawn::new);
    }

    public PacketPlayOutRespawn(final ServerBuffer buf) {
        worldType = buf.readNamespacedKey();
        worldName = buf.readNamespacedKey();
        hashedSeed = buf.readLong();
        gamemode = Gamemode.fromID(buf.readByte());
        previousGamemode = Gamemode.nullableFromID(buf.readByte()).orElse(null);
        isDebug = buf.readBoolean();
        isFlat = buf.readBoolean();
        copyMetadata = buf.readBoolean();
        hasDeathLocation = buf.readBoolean();
        if (hasDeathLocation) {
            deathWorldName = buf.readNamespacedKey();
            deathLocation = buf.readBlockPos();
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
                .writeNamespacedKey(worldType)
                .writeNamespacedKey(worldName)
                .writeLong(hashedSeed)
                .writeByte((byte) gamemode.getID())
                .writeByte((byte) (previousGamemode == null ? -1 : previousGamemode.getID()))
                .writeBoolean(isDebug)
                .writeBoolean(isFlat)
                .writeBoolean(copyMetadata)
                .writeBoolean(hasDeathLocation);
        if (hasDeathLocation) {
            assert deathWorldName != null;
            assert deathLocation != null;
            buf.writeNamespacedKey(deathWorldName)
                    .writeBlockPos(deathLocation);
        }
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutRespawn(new FriendlyByteBuf(serialize()));
    }

}
