package org.machinemc.server.world.blocks;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.Material;
import org.machinemc.api.world.OakSignData;
import org.machinemc.api.world.blocks.BlockEntityBase;
import org.machinemc.api.world.blocks.BlockEntityType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.nbt.NBTByte;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTInt;

import java.awt.*;

public class SignBlock extends BlockTypeImpl implements BlockEntityType {

    public SignBlock() {
        super(NamespacedKey.minecraft("oak_sign"),
                BlockTypeImpl.BlockProperties.builder()
                        .blockHardness(1)
                        .resistance(0.1F)
                        .color(Color.ORANGE)
                        .build(),
                null, true);
    }

    @Override
    public BlockData getBlockData(WorldBlock.@Nullable State block) {
        if(block == null) return Material.OAK_SIGN.createBlockData();
        final NBTCompound compound = block.compound();
        if(!compound.containsKey("rotation")) return Material.OAK_SIGN.createBlockData();
        final int rotation = compound.get("rotation", new NBTInt(0)).value();
        final OakSignData data = (OakSignData) Material.OAK_SIGN.createBlockData();
        data.setRotation(rotation);
        return data;
    }

    @Override
    public boolean hasDynamicVisual() {
        return true;
    }

    @Override
    public void initialize(WorldBlock.State state) {
        final NBTCompound compound = state.compound();
        compound.set("rotation", 0);
    }

    @Override
    public boolean sendsToClient() {
        return true;
    }

    @Override
    public @Nullable BlockEntityBase getBlockEntityBase(WorldBlock.State state) {
        return BlockEntityBase.SIGN;
    }

    @Override
    public @Nullable NBTCompound getClientVisibleNBT(WorldBlock.State state) {
        final NBTCompound compound = getBaseClientVisibleNBT(state);
        if(compound == null) return new NBTCompound();

        compound.set("Text1", "{\"text\":\"" + "Hello World!" + "\"}");
        compound.set("Text2", "{\"text\":\"" + state.position().getX() + "\"}");
        compound.set("Text3", "{\"text\":\"" + state.position().getY() + "\"}");
        compound.set("Text4", "{\"text\":\"" + state.position().getZ() + "\"}");

        compound.set("Color", "black");
        compound.set("GlowingText", new NBTByte(0));
        return compound;
    }

}
