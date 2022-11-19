package me.pesekjak.machine.world.particles.options;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.Writable;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.particles.ParticleOptions;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class BlockOptions implements ParticleOptions, Writable {

    @Getter @Setter
    private BlockData blockData;

    public BlockOptions(FriendlyByteBuf buf) {
        blockData = BlockData.getBlockData(buf.readVarInt());
    }

    @Override
    public NBTCompound toNBT() {
        if(blockData == null) return null;
        return NBT.Compound(nbtCompound ->
                nbtCompound.setString("Name", blockData.getMaterial().getName().toString())
                // TODO Full block data support (properties)
        );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(blockData.getId());
    }

}
