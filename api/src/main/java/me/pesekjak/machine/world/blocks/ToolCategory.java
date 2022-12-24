package me.pesekjak.machine.world.blocks;

import me.pesekjak.machine.world.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.pesekjak.machine.world.Material.*;

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

    private static final Material @NotNull []
            axes = new Material[]{WOODEN_AXE, STONE_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE, GOLDEN_AXE},
            pickaxes = new Material[]{WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE, GOLDEN_PICKAXE},
            shears = new Material[]{Material.SHEARS},
            shovels = new Material[]{WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL, GOLDEN_SHOVEL},
            swords = new Material[]{WOODEN_SWORD, STONE_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, GOLDEN_SWORD},
            hoes = new Material[]{WOODEN_HOE, STONE_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE, GOLDEN_HOE};

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#AXE} category.
     */
    public static Material @NotNull [] getAxes() {
        return axes.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#PICKAXE} category.
     */
    public static Material @NotNull [] getPickaxes() {
        return pickaxes.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#SHEARS} category.
     */
    public static Material @NotNull [] getShears() {
        return shears.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#SHOVEL} category.
     */
    public static Material @NotNull [] getShovels() {
        return shovels.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#SWORD} category.
     */
    public static Material @NotNull [] getSwords() {
        return swords.clone();
    }

    /**
     * @return all materials that are efficient against blocks
     * with {@link ToolCategory#HOE} category.
     */
    public static Material @NotNull [] getHoes() {
        return hoes.clone();
    }

    /**
     * Returns category the given material is effective against if any.
     * @param toolMaterial material to check for
     * @return category the given material is effective against
     */
    public static @Nullable ToolCategory fromTool(@NotNull Material toolMaterial) {
        if(List.of(axes).contains(toolMaterial)) return AXE;
        if(List.of(pickaxes).contains(toolMaterial)) return PICKAXE;
        if(List.of(shears).contains(toolMaterial)) return SHEARS;
        if(List.of(shovels).contains(toolMaterial)) return SHOVEL;
        if(List.of(swords).contains(toolMaterial)) return SWORD;
        if(List.of(hoes).contains(toolMaterial)) return HOE;
        return null;
    }

}
