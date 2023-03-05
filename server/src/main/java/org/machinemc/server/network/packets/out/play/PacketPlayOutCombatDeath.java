package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutCombatDeath extends PacketOut {

    private static final int ID = 0x36;

    private int playerId;
    private int entityId;
    private @NotNull Component deathMessage;

    static {
        register(PacketPlayOutCombatDeath.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutCombatDeath::new);
    }

    public PacketPlayOutCombatDeath(@NotNull ServerBuffer buf) {
        playerId = buf.readVarInt();
        entityId = buf.readInt();
        deathMessage = buf.readComponent();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(playerId)
                .writeInt(entityId)
                .writeComponent(deathMessage)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutCombatDeath(new FriendlyByteBuf(serialize()));
    }

}
