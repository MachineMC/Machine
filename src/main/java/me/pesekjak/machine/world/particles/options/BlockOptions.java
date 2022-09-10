package me.pesekjak.machine.world.particles.options;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.particles.ParticleOptions;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

// TODO Later on Block has implementation,
//  it should allow mapping of block state ids to names
public class BlockOptions implements ParticleOptions {

    @Getter @Setter
    private NamespacedKey blockName = NamespacedKey.minecraft("stone"); // default for now, until Block implementation
    @Getter @Setter
    private int blockStateId;

    public BlockOptions(FriendlyByteBuf buf) {
        blockStateId = buf.readVarInt();
    }

    @Override
    public NBTCompound toNBT() {
        return NBT.Compound(nbtCompound ->
                nbtCompound.setString("Name", blockName.toString())
        );
    }

    @Override
    public FriendlyByteBuf write(FriendlyByteBuf buf) {
        return buf.writeVarInt(blockStateId);
    }

}
