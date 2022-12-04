package me.pesekjak.machine.world.particles.options;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.BlockDataImpl;
import me.pesekjak.machine.world.Material;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class BlockOptionsImpl implements BlockOptions {

    @Getter @Setter
    private BlockData blockData;

    public BlockOptionsImpl(ServerBuffer buf) {
        blockData = BlockDataImpl.getBlockData(buf.readVarInt());
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        if(blockData == null || blockData.getMaterial() == null)
            return NBT.Compound(nbtCompound -> nbtCompound.setString("Name", Material.STONE.getName().toString()));
        return NBT.Compound(nbtCompound ->
                nbtCompound.setString("Name", blockData.getMaterial().getName().toString())
                // TODO Full block data support (properties)
        );
    }

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeVarInt(blockData.getId());
    }

}
