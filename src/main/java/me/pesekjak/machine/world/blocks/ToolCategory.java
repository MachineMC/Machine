package me.pesekjak.machine.world.blocks;

import me.pesekjak.machine.world.Material;

import java.util.List;

import static me.pesekjak.machine.world.Material.*;

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
            axes = new Material[]{WOODEN_AXE, STONE_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE, GOLDEN_AXE},
            pickaxes = new Material[]{WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE, GOLDEN_PICKAXE},
            shears = new Material[]{Material.SHEARS},
            shovels = new Material[]{WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL, GOLDEN_SHOVEL},
            swords = new Material[]{WOODEN_SWORD, STONE_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, GOLDEN_SWORD},
            hoes = new Material[]{WOODEN_HOE, STONE_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE, GOLDEN_HOE};

    public static Material[] getAxes() {
        return axes.clone();
    }

    public static Material[] getPickaxes() {
        return pickaxes.clone();
    }

    public static Material[] getShears() {
        return shears.clone();
    }

    public static Material[] getShovels() {
        return shovels.clone();
    }

    public static Material[] getSwords() {
        return swords.clone();
    }

    public static Material[] getHoes() {
        return hoes.clone();
    }

    public static ToolCategory fromTool(Material toolMaterial) {
        if(List.of(axes).contains(toolMaterial)) return AXE;
        if(List.of(pickaxes).contains(toolMaterial)) return PICKAXE;
        if(List.of(shears).contains(toolMaterial)) return SHEARS;
        if(List.of(shovels).contains(toolMaterial)) return SHOVEL;
        if(List.of(swords).contains(toolMaterial)) return SWORD;
        if(List.of(hoes).contains(toolMaterial)) return HOE;
        return null;
    }

}
