package me.pesekjak.machine.world.blocks;

import lombok.Getter;
import me.pesekjak.machine.world.Material;

import static me.pesekjak.machine.world.Material.*;

public enum ToolLevel {

    NOTHING(1),
    WOOD(2,
            WOODEN_AXE, WOODEN_HOE, WOODEN_PICKAXE,
            WOODEN_SWORD, WOODEN_SHOVEL),
    STONE(4,
            STONE_AXE, STONE_HOE, STONE_PICKAXE,
            STONE_SWORD, STONE_SHOVEL),
    IRON(6,
            IRON_AXE, IRON_HOE, IRON_PICKAXE,
            IRON_SWORD, IRON_SHOVEL),
    DIAMOND(8,
            DIAMOND_AXE, DIAMOND_HOE, DIAMOND_PICKAXE,
            DIAMOND_SWORD, DIAMOND_SHOVEL),
    NETHERITE(9,
            NETHERITE_AXE, NETHERITE_HOE, NETHERITE_PICKAXE,
            NETHERITE_SWORD, NETHERITE_SHOVEL),
    GOLD(12,
            GOLDEN_AXE, GOLDEN_HOE, GOLDEN_PICKAXE,
            GOLDEN_SWORD, GOLDEN_SHOVEL);

    @Getter
    private final double speed;
    private final Material[] materials;

    ToolLevel(double speed, Material... materials) {
        this.speed = speed;
        this.materials = materials;
    }

    public Material[] getMaterials() {
        return materials.clone();
    }

    public static ToolLevel fromMaterial(Material material) {
        for(ToolLevel level : values()) {
            for(Material tool : level.materials) {
                if(material == tool) return level;
            }
        }
        return null;
    }

    public static ToolLevel fromSpeed(double speed) {
        for(ToolLevel level : values()) {
            if(level.speed == speed) return level;
        }
        return null;
    }

    public static double shearsSpeed(Material material) {
        return switch (material) {
            case VINE, GLOW_LICHEN -> 1;
            case WHITE_WOOL, ORANGE_WOOL, MAGENTA_WOOL,
                    LIGHT_BLUE_WOOL, YELLOW_WOOL, LIME_WOOL,
                    PINK_WOOL, GRAY_WOOL, LIGHT_GRAY_WOOL,
                    CYAN_WOOL, PURPLE_WOOL, BLUE_WOOL,
                    BROWN_WOOL, GREEN_WOOL, RED_WOOL,
                    BLACK_WOOL -> 5;
            case COBWEB, ACACIA_LEAVES, AZALEA_LEAVES,
                    BIRCH_LEAVES, DARK_OAK_LEAVES,
                    FLOWERING_AZALEA_LEAVES, JUNGLE_LEAVES,
                    MANGROVE_LEAVES, OAK_LEAVES,
                    SPRUCE_LEAVES-> 15;
            default -> 2;
        };
    }

    public static double swordSpeed(Material material) {
        return material == Material.COBWEB ? 15 : 1.5;
    }

}
