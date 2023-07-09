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
package org.machinemc.api.world.blocks;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Range;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Material;

import java.util.Optional;

/**
 * Represents a base block entity types that are required on client side
 * to make the visuals of these types display correctly.
 */
public enum BlockEntityBase {

    FURNACE(Material.FURNACE),
    CHEST(Material.CHEST),
    TRAPPED_CHEST(Material.TRAPPED_CHEST),
    ENDER_CHEST(Material.ENDER_CHEST),
    JUKEBOX(Material.JUKEBOX),
    DISPENSER(Material.DISPENSER),
    DROPPER(Material.DROPPER),
    SIGN(Material.OAK_SIGN, Material.SPRUCE_SIGN, Material.BIRCH_SIGN, Material.ACACIA_SIGN,
            Material.JUNGLE_SIGN, Material.DARK_OAK_SIGN, Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.ACACIA_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN, Material.DARK_OAK_WALL_SIGN, Material.CRIMSON_SIGN,
            Material.CRIMSON_WALL_SIGN, Material.WARPED_SIGN, Material.WARPED_WALL_SIGN,
            Material.MANGROVE_SIGN, Material.MANGROVE_WALL_SIGN),
    // @Deprecated HANGING_SIGN, TODO for future versions
    MOB_SPAWNER(Material.SPAWNER),
    PISTON(Material.MOVING_PISTON),
    BREWING_STAND(Material.BREWING_STAND),
    ENCHANTING_TABLE(Material.ENCHANTING_TABLE),
    END_PORTAL(Material.END_PORTAL),
    BEACON(Material.BEACON),
    SKULL(Material.SKELETON_SKULL, Material.SKELETON_WALL_SKULL, Material.CREEPER_HEAD,
            Material.CREEPER_WALL_HEAD, Material.DRAGON_HEAD, Material.DRAGON_WALL_HEAD,
            Material.ZOMBIE_HEAD, Material.ZOMBIE_WALL_HEAD, Material.WITHER_SKELETON_SKULL,
            Material.WITHER_SKELETON_WALL_SKULL, Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD),
    DAYLIGHT_DETECTOR(Material.DAYLIGHT_DETECTOR),
    HOPPER(Material.HOPPER),
    COMPARATOR(Material.COMPARATOR),
    BANNER(Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER, Material.LIME_BANNER,
            Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER,
            Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER,
            Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER,
            Material.BLACK_BANNER, Material.WHITE_WALL_BANNER, Material.ORANGE_WALL_BANNER,
            Material.MAGENTA_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER,
            Material.YELLOW_WALL_BANNER, Material.LIME_WALL_BANNER, Material.PINK_WALL_BANNER,
            Material.GRAY_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER, Material.CYAN_WALL_BANNER,
            Material.PURPLE_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_WALL_BANNER,
            Material.GREEN_WALL_BANNER, Material.RED_WALL_BANNER, Material.BLACK_WALL_BANNER),
    STRUCTURE_BLOCK(Material.STRUCTURE_BLOCK),
    END_GATEWAY(Material.END_GATEWAY),
    COMMAND_BLOCK(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK),
    SHULKER_BOX(Material.SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
            Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
            Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX,
            Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX),
    BED(Material.RED_BED, Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
            Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED,
            Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED,
            Material.PINK_BED, Material.PURPLE_BED, Material.WHITE_BED, Material.YELLOW_BED),
    CONDUIT(Material.CONDUIT),
    BARREL(Material.BARREL),
    SMOKER(Material.SMOKER),
    BLAST_FURNACE(Material.BLAST_FURNACE),
    LECTERN(Material.LECTERN),
    BELL(Material.BELL),
    JIGSAW(Material.JIGSAW),
    CAMPFIRE(Material.CAMPFIRE, Material.SOUL_CAMPFIRE),
    BEEHIVE(Material.BEEHIVE),
    SCULK_SENSOR(Material.SCULK_SENSOR),
    SCULK_CATALYST(Material.SCULK_CATALYST),
    SCULK_SHRIEKER(Material.SCULK_SHRIEKER);
    // @Deprecated CHISELED_BOOKSHELF; TODO for future versions

    private final Material[] materials;

    BlockEntityBase(final Material... materials) {
        this.materials = materials;
    }

    /**
     * @return all materials supported by this block entity base
     */
    public Material[] getSupportedMaterials() {
        return materials.clone();
    }

    /**
     * Returns whether this block entity base supports the given material.
     * @param material material
     * @return whether the given material is supported by this block entity base
     */
    public boolean supports(final Material material) {
        for (final Material supported : materials) {
            if (supported.equals(material)) return true;
        }
        return false;
    }

    /**
     * @return numeric id of the block entity base used by Minecraft protocol.
     */
    public @Range(from = 0, to = 37) int getID() {
        return ordinal();
    }

    /**
     * @return name of the base
     */
    public NamespacedKey getName() {
        return NamespacedKey.minecraft(this.name().toLowerCase());
    }

    /**
     * Returns difficulty from its numeric id.
     * @param id id of the difficulty
     * @return difficulty for given id
     */
    public static BlockEntityBase fromID(final @Range(from = 0, to = 37) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported block entity base");
        return values()[id];
    }

    /**
     * Returns block entity base of given name.
     * @param name name of the block entity base
     * @return block entity base with given name
     */
    public static Optional<BlockEntityBase> getByName(final String name) {
        for (final BlockEntityBase value : values()) {
            if (value.name().equalsIgnoreCase(name)) return Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * Returns block entity base of given material.
     * @param material material supported by the block entity base
     * @return block entity base of given material
     */
    public static Optional<BlockEntityBase> getByMaterial(final Material material) {
        for (final BlockEntityBase value : values()) {
            for (final Material supported : value.materials)
                if (supported.equals(material)) return Optional.of(value);
        }
        return Optional.empty();
    }

}
