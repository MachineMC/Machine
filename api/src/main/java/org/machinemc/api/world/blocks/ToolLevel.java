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

import lombok.Getter;
import org.machinemc.api.world.Material;

import java.util.Optional;
import java.util.Objects;

import static org.machinemc.api.world.Material.*;

/**
 * Represents mining speeds of different materials.
 */
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

    ToolLevel(final double speed, final Material... materials) {
        this.speed = speed;
        this.materials = materials;
    }

    /**
     * @return all materials of the tool level
     */
    public Material[] getMaterials() {
        return materials.clone();
    }

    /**
     * Returns the tool level of given material.
     * @param material material to check for
     * @return tool level of given material
     */
    public static Optional<ToolLevel> fromMaterial(final Material material) {
        Objects.requireNonNull(material, "Materials of the tool level can not be null");
        for (final ToolLevel level : values()) {
            for (final Material tool : level.materials) {
                if (material == tool) return Optional.of(level);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the tool level from the speed.
     * @param speed general mining speed of the tool level
     * @return tool level matching the given speed
     */
    public static Optional<ToolLevel> fromSpeed(final double speed) {
        for (final ToolLevel level : values()) {
            if (level.speed == speed) return Optional.of(level);
        }
        return Optional.empty();
    }

    /**
     * Calculates speed of material as shears.
     * @param material material to check for
     * @return mining speed of the material as shears
     */
    public static double shearsSpeed(final Material material) {
        Objects.requireNonNull(material, "Materials of the tool level can not be null");
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
                    SPRUCE_LEAVES -> 15;
            default -> 2;
        };
    }

    /**
     * Calculates speed of material as sword.
     * @param material material to check for
     * @return mining speed of the material as sword
     */
    public static double swordSpeed(final Material material) {
        Objects.requireNonNull(material, "Materials of the tool level can not be null");
        return material == Material.COBWEB ? 15 : 1.5;
    }

}
