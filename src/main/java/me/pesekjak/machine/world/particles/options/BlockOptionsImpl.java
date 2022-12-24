package me.pesekjak.machine.world.particles.options;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.BlockDataImpl;
import me.pesekjak.machine.world.Material;
import me.pesekjak.machine.world.particles.ParticleFactory;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

/**
 * Default block options implementation.
 */
@Data
@AllArgsConstructor
public class BlockOptionsImpl implements BlockOptions {

    private static final Material DEFAULT_LOOK = Material.STONE;

    private @NotNull BlockData blockData;

    static {
        ParticleFactory.registerOption(BlockOptions.class, BlockOptionsImpl::new);
    }

    public BlockOptionsImpl() {
        this.blockData = Material.STONE.createBlockData();
    }

    public BlockOptionsImpl(@NotNull ServerBuffer buf) {
        final BlockData blockData = BlockDataImpl.getBlockData(buf.readVarInt());
        this.blockData = blockData != null ? blockData : DEFAULT_LOOK.createBlockData();
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        if(blockData.getMaterial() == null)
            return NBT.Compound(nbtCompound -> nbtCompound.setString("Name", DEFAULT_LOOK.getName().toString()));
        return NBT.Compound(nbtCompound ->
                nbtCompound.setString("Name", blockData.getMaterial().getName().toString())
                // TODO Full block data support (properties)
        );
    }

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeVarInt(blockData.getId());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public BlockOptions clone() {
        final ServerBuffer buf = new FriendlyByteBuf().write(this);
        return new BlockOptionsImpl(buf);
    }
}
