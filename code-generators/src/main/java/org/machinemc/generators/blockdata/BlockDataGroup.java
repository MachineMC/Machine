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
package org.machinemc.generators.blockdata;

import lombok.Getter;
import org.machinemc.generators.CodeGenerator;

@Getter
public enum BlockDataGroup {

    BANNER(
            new String[]{"rotation"},
            "banner"
    ),
    BED(new String[]{"facing", "occupied", "part"}, "bed"),
    CANDLE(
            new String[]{"candles", "lit", "waterlogged"},
            new String[]{"black_candle", "blue_candle", "brown_candle", "candle", "cyan_candle",
                    "gray_candle", "green_candle", "light_blue_candle", "light_gray_candle",
                    "lime_candle", "magenta_candle", "orange_candle", "pink_candle",
                    "purple_candle", "red_candle", "white_candle", "yellow_candle"}
    ),
    CANDLE_CAKE(new String[]{"lit"}, "candle_cake"),
    COMMAND_BLOCK(
            new String[]{"conditional", "facing"},
            new String[]{"chain_command_block", "command_block", "repeating_command_block"}
    ),
    CORAL_WALL_FAN(
            new String[]{"facing", "waterlogged"},
            new String[]{"brain_coral_wall_fan", "bubble_coral_wall_fan", "dead_brain_coral_wall_fan",
                    "dead_bubble_coral_wall_fan", "dead_fire_coral_wall_fan", "dead_horn_coral_wall_fan",
                    "dead_tube_coral_wall_fan", "fire_coral_wall_fan",
                    "horn_coral_wall_fan", "tube_coral_wall_fan"}
    ),
    DOOR(
            new String[]{"facing", "half", "hinge", "open", "powered"},
            new String[]{"acacia_door", "birch_door", "crimson_door", "dark_oak_door", "iron_door", "jungle_door",
                    "mangrove_door", "oak_door", "spruce_door", "warped_door"}
    ),
    FENCE(
            new String[]{"east", "north", "south", "waterlogged", "west"},
            new String[]{"acacia_fence", "birch_fence", "crimson_fence",
                    "dark_oak_fence", "jungle_fence", "mangrove_fence",
                    "nether_brick_fence", "oak_fence", "spruce_fence", "warped_fence"}
    ),
    FENCE_GATE(
            new String[]{"facing", "in_wall", "open", "powered"},
            new String[]{"acacia_fence_gate", "birch_fence_gate",
                    "crimson_fence_gate", "dark_oak_fence_gate",
                    "jungle_fence_gate", "mangrove_fence_gate", "oak_fence_gate",
                    "spruce_fence_gate", "warped_fence_gate"}
    ),
    GLASS_PANE(
            new String[]{"east", "north", "south", "waterlogged", "west"},
            new String[]{"black_stained_glass_pane", "blue_stained_glass_pane", "brown_stained_glass_pane",
                    "cyan_stained_glass_pane", "glass_pane", "gray_stained_glass_pane", "green_stained_glass_pane",
                    "light_blue_stained_glass_pane", "light_gray_stained_glass_pane", "lime_stained_glass_pane",
                    "magenta_stained_glass_pane", "orange_stained_glass_pane", "pink_stained_glass_pane",
                    "purple_stained_glass_pane", "red_stained_glass_pane", "white_stained_glass_pane",
                    "yellow_stained_glass_pane"}
    ),
    LEAVES(
            new String[]{"distance", "persistent", "waterlogged"},
            new String[]{"acacia_leaves", "azalea_leaves", "birch_leaves", "dark_oak_leaves", "flowering_azalea_leaves",
                    "jungle_leaves", "mangrove_leaves", "oak_leaves", "spruce_leaves"}
    ),
    PISTON(
            new String[]{"extended", "facing"},
            new String[]{"piston", "sticky_piston"}
    ),
    POTTED(
            new String[0],
            new String[]{"potted_acacia_sapling", "potted_allium", "potted_azalea_bush", "potted_azure_bluet",
                    "potted_bamboo", "potted_birch_sapling", "potted_blue_orchid", "potted_brown_mushroom",
                    "potted_cactus", "potted_cornflower", "potted_crimson_fungus", "potted_crimson_roots",
                    "potted_dandelion", "potted_dark_oak_sapling", "potted_dead_bush", "potted_fern",
                    "potted_flowering_azalea_bush", "potted_jungle_sapling", "potted_lily_of_the_valley",
                    "potted_mangrove_propagule", "potted_oak_sapling", "potted_orange_tulip", "potted_oxeye_daisy",
                    "potted_pink_tulip", "potted_poppy", "potted_red_mushroom", "potted_red_tulip",
                    "potted_spruce_sapling", "potted_warped_fungus", "potted_warped_roots",
                    "potted_white_tulip", "potted_wither_rose"}
    ),
    RAIL(
            new String[]{"shape", "waterlogged"},
            new String[]{"activator_rail", "detector_rail", "powered_rail", "rail"}
    ),
    REDSTONE_RAIL(
            new String[]{"powered", "shape", "waterlogged"},
            new String[]{"activator_rail", "detector_rail", "powered_rail"}
    ),
    SAPLING(
            new String[]{"stage"},
            new String[]{"acacia_sapling", "birch_sapling", "dark_oak_sapling", "jungle_sapling",
                    "oak_sapling", "spruce_sapling"}
    ),
    SLAB(
            new String[]{"type", "waterlogged"},
            new String[]{"acacia_slab", "andesite_slab", "birch_slab", "blackstone_slab",
                    "brick_slab", "cobbled_deepslate_slab", "cobblestone_slab", "crimson_slab",
                    "cut_copper_slab", "cut_red_sandstone_slab", "cut_sandstone_slab",
                    "dark_oak_slab", "dark_prismarine_slab", "deepslate_brick_slab",
                    "deepslate_tile_slab", "diorite_slab", "end_stone_brick_slab",
                    "exposed_cut_copper_slab", "granite_slab", "jungle_slab", "mangrove_slab",
                    "mossy_cobblestone_slab", "mossy_stone_brick_slab", "mud_brick_slab",
                    "nether_brick_slab", "oak_slab", "oxidized_cut_copper_slab", "petrified_oak_slab",
                    "polished_andesite_slab", "polished_blackstone_brick_slab", "polished_blackstone_slab",
                    "polished_deepslate_slab", "polished_diorite_slab", "polished_granite_slab",
                    "prismarine_brick_slab", "prismarine_slab", "purpur_slab", "quartz_slab",
                    "red_nether_brick_slab", "red_sandstone_slab", "sandstone_slab", "smooth_quartz_slab",
                    "smooth_red_sandstone_slab", "smooth_sandstone_slab", "smooth_stone_slab",
                    "spruce_slab", "stone_brick_slab", "stone_slab", "warped_slab", "waxed_cut_copper_slab",
                    "waxed_exposed_cut_copper_slab", "waxed_oxidized_cut_copper_slab",
                    "waxed_weathered_cut_copper_slab", "weathered_cut_copper_slab"}
    ),
    SIGN(
            new String[]{"rotation", "waterlogged"},
            new String[]{"acacia_sign", "birch_sign", "crimson_sign", "dark_oak_sign", "jungle_sign",
                    "mangrove_sign", "oak_sign", "spruce_sign", "warped_sign"}
    ),
    TRAP_DOOR(
            new String[]{"facing", "half", "open", "powered", "waterlogged"},
            new String[]{"acacia_trapdoor", "birch_trapdoor", "crimson_trapdoor", "dark_oak_trapdoor", "iron_trapdoor",
                    "jungle_trapdoor", "mangrove_trapdoor", "oak_trapdoor", "spruce_trapdoor", "warped_trapdoor"}
    ),
    STAIRS(
            new String[]{"facing", "half", "shape", "waterlogged"},
            new String[]{"acacia_stairs", "andesite_stairs", "birch_stairs", "blackstone_stairs", "brick_stairs",
                    "cobbled_deepslate_stairs", "cobblestone_stairs", "crimson_stairs", "cut_copper_stairs",
                    "dark_oak_stairs", "dark_prismarine_stairs", "deepslate_brick_stairs", "deepslate_tile_stairs",
                    "diorite_stairs", "end_stone_brick_stairs", "exposed_cut_copper_stairs", "granite_stairs",
                    "jungle_stairs", "mangrove_stairs", "mossy_cobblestone_stairs", "mossy_stone_brick_stairs",
                    "mud_brick_stairs", "nether_brick_stairs", "oak_stairs", "oxidized_cut_copper_stairs",
                    "polished_andesite_stairs", "polished_blackstone_brick_stairs", "polished_blackstone_stairs",
                    "polished_deepslate_stairs", "polished_diorite_stairs", "polished_granite_stairs",
                    "prismarine_brick_stairs", "prismarine_stairs", "purpur_stairs", "quartz_stairs",
                    "red_nether_brick_stairs", "red_sandstone_stairs", "sandstone_stairs", "smooth_quartz_stairs",
                    "smooth_red_sandstone_stairs", "smooth_sandstone_stairs", "spruce_stairs", "stone_brick_stairs",
                    "stone_stairs", "warped_stairs", "waxed_cut_copper_stairs", "waxed_exposed_cut_copper_stairs",
                    "waxed_oxidized_cut_copper_stairs", "waxed_weathered_cut_copper_stairs",
                    "weathered_cut_copper_stairs"}
    ),
    WALL(
            new String[]{"east", "north", "south", "up", "waterlogged", "west"},
            new String[]{"andesite_wall", "blackstone_wall", "brick_wall", "cobbled_deepslate_wall",
                    "cobblestone_wall", "deepslate_brick_wall", "deepslate_tile_wall", "diorite_wall",
                    "end_stone_brick_wall", "granite_wall", "mossy_cobblestone_wall", "mossy_stone_brick_wall",
                    "mud_brick_wall", "nether_brick_wall", "polished_blackstone_brick_wall",
                    "polished_blackstone_wall", "polished_deepslate_wall", "prismarine_wall",
                    "red_nether_brick_wall", "red_sandstone_wall", "sandstone_wall", "stone_brick_wall"}
    ),
    WALL_BANNER(new String[]{"facing"}, "wall_banner"),
    WALL_SIGN(
            new String[]{"facing", "waterlogged"},
            new String[]{"acacia_wall_sign", "birch_wall_sign", "crimson_wall_sign", "dark_oak_wall_sign",
                    "jungle_wall_sign", "mangrove_wall_sign", "oak_wall_sign", "spruce_wall_sign",
                    "warped_wall_sign"}
    );

    private final String[] properties;
    private final String[] blocks;

    BlockDataGroup(final String[] properties, final String[] blocks) {
        this.properties = properties;
        this.blocks = new String[blocks.length];
        for (int i = 0; i < blocks.length; i++)
            this.blocks[i] = "minecraft:" + blocks[i];
    }

    BlockDataGroup(final String[] properties, final String baseName) {
        final String[] colors = {"black", "blue", "brown", "cyan", "gray",
                "green", "light_blue", "light_gray", "lime", "magenta", "orange", "pink", "purple",
                "red", "white", "yellow"};
        this.properties = properties;
        blocks = new String[16];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = "minecraft:" + colors[i] + "_" + baseName;
        }
    }

    /**
     * @return path of the interface for this group
     */
    public String getPath() {
        return "org.machinemc.api.world.blockdata." + CodeGenerator.toCamelCase(name(), true) + "DataGroup";
    }

}
