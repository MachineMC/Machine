package org.machinemc.api.world.blocks;

import org.machinemc.api.world.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.machinemc.api.world.Material.*;

/**
 * Represents mining category of block.
 */
public enum ToolCategory {

    AXE,
    PICKAXE,
    SHEARS,
    SHOVEL,
    SWORD,
    HOE,
    OTHER, // all tools are equally efficient
    INSTANT, // instantly breaks
    NONE; // unbreakable

    private static final Material[]
            AXES_MATERIALS = new Material[]{WOODEN_AXE, STONE_AXE, IRON_AXE, DIAMOND_AXE,
            NETHERITE_AXE, GOLDEN_AXE},

            PICKAXES_MATERIALS = new Material[]{WOODEN_PICKAXE, STONE_PICKAXE,
                    IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE, GOLDEN_PICKAXE},

            SHEARS_MATERIALS = new Material[]{Material.SHEARS},

            SHOVELS_MATERIALS = new Material[]{WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL,
                    DIAMOND_SHOVEL, NETHERITE_SHOVEL, GOLDEN_SHOVEL},

            SWORDS_MATERIALS = new Material[]{WOODEN_SWORD, STONE_SWORD, IRON_SWORD,
                    DIAMOND_SWORD, NETHERITE_SWORD, GOLDEN_SWORD},

            HOES_MATERIALS = new Material[]{WOODEN_HOE, STONE_HOE, IRON_HOE,
                    DIAMOND_HOE, NETHERITE_HOE, GOLDEN_HOE};

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#AXE} category.
     */
    public static Material[] getAxes() {
        return AXES_MATERIALS.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#PICKAXE} category.
     */
    public static Material[] getPickaxes() {
        return PICKAXES_MATERIALS.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#SHEARS} category.
     */
    public static Material[] getShears() {
        return SHEARS_MATERIALS.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#SHOVEL} category.
     */
    public static Material[] getShovels() {
        return SHOVELS_MATERIALS.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#SWORD} category.
     */
    public static Material[] getSwords() {
        return SWORDS_MATERIALS.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#HOE} category.
     */
    public static Material[] getHoes() {
        return HOES_MATERIALS.clone();
    }

    /**
     * Returns category the given material is effective against if any.
     * @param toolMaterial material to check for
     * @return category the given material is effective against
     */
    public static @Nullable ToolCategory fromTool(final Material toolMaterial) {
        if (List.of(AXES_MATERIALS).contains(toolMaterial)) return AXE;
        if (List.of(PICKAXES_MATERIALS).contains(toolMaterial)) return PICKAXE;
        if (List.of(SHEARS_MATERIALS).contains(toolMaterial)) return SHEARS;
        if (List.of(SHOVELS_MATERIALS).contains(toolMaterial)) return SHOVEL;
        if (List.of(SWORDS_MATERIALS).contains(toolMaterial)) return SWORD;
        if (List.of(HOES_MATERIALS).contains(toolMaterial)) return HOE;
        return null;
    }

}
