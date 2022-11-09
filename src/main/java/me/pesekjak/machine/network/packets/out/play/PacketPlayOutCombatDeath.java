package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class PacketPlayOutCombatDeath extends PacketOut {

    private static final int ID = 0x36;

    @Getter @Setter
    private int playerId;
    @Getter @Setter
    private int entityId;
    @Getter @Setter
    private Component deathMessage;

    static {
        register(PacketPlayOutCombatDeath.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutCombatDeath::new);
    }

    public PacketPlayOutCombatDeath(FriendlyByteBuf buf) {
        playerId = buf.readVarInt();
        entityId = buf.readInt();
        deathMessage = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(playerId)
                .writeInt(entityId)
                .writeComponent(deathMessage)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutCombatDeath(new FriendlyByteBuf(serialize()));
    }

}
