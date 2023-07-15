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
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutRespawn extends PacketOut {

    private static final int ID = 0x41;

    /**
     * Used in normal respawns (after death), keeps no data.
     */
    public static final byte KEEP_NOTHING = 0x00;

    /**
     * Used when exiting the end poem/credits, keeps the attributes.
     */
    public static final byte KEEP_ATTRIBUTES = 0x01;

    public static final byte KEEP_METADATA = 0x02;

    /**
     * Used in other dimension changes (portals or teleports), keep all data.
     */
    public static final byte KEEP_ALL_DATA = KEEP_ATTRIBUTES | KEEP_METADATA;

    private NamespacedKey worldType, worldName;
    private long hashedSeed;
    private Gamemode gamemode;
    private @Nullable Gamemode previousGamemode;
    private boolean isDebug, isFlat;
    private byte dataKept;
    private boolean hasDeathLocation;
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
        previousGamemode = Gamemode.nullableFromID(buf.readByte());
        isDebug = buf.readBoolean();
        isFlat = buf.readBoolean();
        dataKept = buf.readByte();
        hasDeathLocation = buf.readBoolean();
        if (hasDeathLocation) {
            deathWorldName = buf.readNamespacedKey();
            deathLocation = buf.readBlockPos();
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
                .writeNamespacedKey(worldType)
                .writeNamespacedKey(worldName)
                .writeLong(hashedSeed)
                .writeByte((byte) gamemode.getId())
                .writeByte((byte) (previousGamemode == null ? -1 : previousGamemode.getId()))
                .writeBoolean(isDebug)
                .writeBoolean(isFlat)
                .writeByte(dataKept)
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
