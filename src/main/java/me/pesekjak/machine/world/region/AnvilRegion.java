package me.pesekjak.machine.world.region;

import me.pesekjak.machine.chunk.Chunk;
import me.pesekjak.machine.chunk.DynamicChunk;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.blocks.BlockType;
import org.jetbrains.annotations.Range;
import org.jglrxavpok.hephaistos.data.RandomAccessFileSource;
import org.jglrxavpok.hephaistos.mca.AnvilException;
import org.jglrxavpok.hephaistos.mca.BlockState;
import org.jglrxavpok.hephaistos.mca.ChunkColumn;
import org.jglrxavpok.hephaistos.mca.RegionFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * Region implementing Minecraft's Anvil file region format.
 */
public class AnvilRegion extends Region {

    private static final String MODE = "rw";

    private RegionFile regionFile;

    public AnvilRegion(World world, File file, int x, int z) throws IOException, AnvilException {
        super(world, x, z);
        regionFile = new RegionFile(new RandomAccessFileSource(new RandomAccessFile(file, MODE)), x, z, 0, world.getDimensionType().getHeight());
    }

    @Override
    public void save() {
        try {
            for(Chunk[] chunks : grid) {
                for(Chunk chunk : chunks) {
                    if(chunk == null) continue;
                    ChunkColumn column = regionFile.getChunk(chunk.getChunkX(), chunk.getChunkZ());
                    if(column == null) continue;
                    fillColumn(column, chunk);
                    regionFile.writeColumn(column);
                }
            }
            regionFile.flushCachedChunks();
        } catch (IOException | AnvilException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Chunk getChunk(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z) {
        if(grid[x][z] == null) {
            try {
                loadChunk(x, z);
            } catch (AnvilException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return grid[x][z];
    }

    /**
     * Loads chunk from the region file to the memory
     * @param x x coordinate of the chunk in the region
     * @param z z coordinate of the chunk in the region
     */
    public void loadChunk(int x, int z) throws AnvilException, IOException {
        final int worldX = this.x * 32 + x;
        final int worldZ = this.z * 32 + z;
        ChunkColumn column;
        Chunk chunk = new DynamicChunk(world, worldX, worldZ);
        if(shouldGenerate(x, z)) {
            column = regionFile.getOrCreateChunk(worldX, worldZ);
            column.setYRange(0, world.getDimensionType().getHeight());
            fillColumn(column, BlockState.AIR);
        } else {
            column = regionFile.getChunk(worldX, worldZ);
            if(column == null) throw new IllegalStateException();
            column.setYRange(0, getWorld().getDimensionType().getHeight());
        }
        fillChunk(chunk, column);
        grid[x][z] = chunk;
    }

    @Override
    public boolean shouldGenerate(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z) {
        try {
            return regionFile.getChunk(this.x * 32 + x, this.z * 32 + z) == null;
        } catch (AnvilException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fills the chunk column with single block state
     * @param chunkColumn chunk column to fill
     * @param state new state
     */
    private void fillColumn(ChunkColumn chunkColumn, BlockState state) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunkColumn.getMaxY(); y++) {
                    chunkColumn.setBlockState(x, y, z, state);
                }
            }
        }
    }

    /**
     * Fills the chunk column with a block states from a chunk.
     * @param column column to fill
     * @param chunk chunk with the data
     */
    private void fillColumn(ChunkColumn column, Chunk chunk) {
        if(column.getMaxY() != chunk.getWorld().getDimensionType().getHeight())
            throw new IllegalStateException();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < column.getMaxY(); y++) {
                    final BlockType block = chunk.getBlock(x, y, z).getBlockType();
                    column.setBlockState(x, y, z,
                            !block.getName().toString().equals(BlockState.AIR.getName()) ?
                                    createBlockState(block) :
                                    BlockState.AIR
                    );
                }
            }
        }
    }

    /**
     * Fills the chunk with a block states from a chunk column.
     * @param chunk chunk to fill
     * @param column column with the data
     */
    private void fillChunk(Chunk chunk, ChunkColumn column) {
        if(column.getMaxY() != chunk.getWorld().getDimensionType().getHeight())
            throw new IllegalStateException();
        final Map<String, BlockType> blockMap = new HashMap<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < column.getMaxY(); y++) {
                    final String name = column.getBlockState(x, y, z).getName();
                    if(blockMap.get(name) == null)
                        blockMap.put(name, world.getServer().getBlockManager().getBlockType(NamespacedKey.parse(name)));
                    chunk.setBlock(x, y, z, blockMap.get(name), BlockType.CreateReason.SET, BlockType.DestroyReason.REMOVED, null);
                }
            }
        }
    }

    /**
     * Converts the BlockType to a BlockState
     * @param blockType block type to convert
     * @return converted block state
     */
    private static BlockState createBlockState(BlockType blockType) {
        return new BlockState(blockType.getName().toString());
    }

}
