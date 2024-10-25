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
package org.machinemc.server.world.blocks;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.Material;
import org.machinemc.api.world.OakSignData;
import org.machinemc.api.world.blocks.BlockEntityBase;
import org.machinemc.api.world.blocks.BlockEntityType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTInt;
import org.machinemc.nbt.NBTList;
import org.machinemc.nbt.NBTString;
import org.machinemc.scriptive.style.HexColor;

import java.awt.*;
import java.util.Optional;

public class SignBlock extends BlockTypeImpl implements BlockEntityType {

    public SignBlock() {
        super(NamespacedKey.minecraft("oak_sign"),
                BlockTypeImpl.BlockProperties.builder()
                        .blockHardness(1)
                        .resistance(0.1F)
                        .color(new HexColor(Color.ORANGE))
                        .build(),
                null,
                true);
    }

    @Override
    public BlockData getBlockData(final WorldBlock.@Nullable State block) {
        if (block == null) return Material.OAK_SIGN.createBlockData();
        final NBTCompound compound = block.compound();
        if (!compound.containsKey("rotation")) return Material.OAK_SIGN.createBlockData();
        final int rotation = compound.getValue("rotation", 0);
        final OakSignData data = (OakSignData) Material.OAK_SIGN.createBlockData();
        data.setRotation(rotation);
        return data;
    }

    @Override
    public boolean hasDynamicVisual() {
        return true;
    }

    @Override
    public void initialize(final WorldBlock.State state) {
        final NBTCompound compound = state.compound();
        compound.set("rotation", 0);
    }

    @Override
    public boolean sendsToClient() {
        return true;
    }

    @Override
    public Optional<BlockEntityBase> getBlockEntityBase(final WorldBlock.State state) {
        return Optional.of(BlockEntityBase.SIGN);
    }

    @Override
    public Optional<NBTCompound> getClientVisibleNBT(final WorldBlock.State state) {
        return getBaseClientVisibleNBT(state).map(compound -> {
            final NBTCompound front = new NBTCompound();
            final NBTList text = new NBTList();
            text.add(new NBTString("{\"text\":\"" + "Hello World!" + "\"}"));
            text.add(new NBTString("{\"text\":\"" + state.position().getX() + "\"}"));
            text.add(new NBTString("{\"text\":\"" + state.position().getY() + "\"}"));
            text.add(new NBTString("{\"text\":\"" + state.position().getZ() + "\"}"));
            front.set("messages", text);
            compound.set("front_text", front);
            return compound;
        });
    }

}
