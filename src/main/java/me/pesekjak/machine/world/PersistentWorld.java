package me.pesekjak.machine.world;

import lombok.Getter;
import me.pesekjak.machine.utils.NBTUtils;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.dimensions.DimensionType;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Represents a world stored in a folder.
 */
public class PersistentWorld extends World {

    public final static String DEFAULT_WORLD_FOLDER = "level";
    public final static String DEFAULT_PLAYER_DATA_FOLDER = "playerdata";

    @Getter
    private final String folderName;
    @Getter
    private final File playerDataFolder;

    private PersistentWorld(String folderName,
            NamespacedKey name,
            UUID uuid,
            DimensionType dimensionType,
            long seed,
            Difficulty difficulty,
            Location worldSpawn) {
        super(name, uuid, dimensionType, seed, difficulty, worldSpawn);
        setWorldSpawn(worldSpawn);
        this.folderName = folderName;
        playerDataFolder = new File(folderName, DEFAULT_PLAYER_DATA_FOLDER);
    }

    public PersistentWorld(String folderName, World world) {
        this(folderName,
                world.getName(),
                world.getUuid(),
                world.getDimensionType(),
                world.getSeed(),
                world.getDifficulty(),
                new Location(0, 0, 0, world)
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getOrCreatePlayerDataFolder() {
        File folder = getPlayerDataFolder();
        if (!folder.exists())
            folder.mkdirs();
        return folder;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public NBTCompound getPlayerData(UUID uuid) {
        try {
            File playerDataFile = new File(getOrCreatePlayerDataFolder(), uuid + ".dat");
            if (!playerDataFile.exists()) {
                playerDataFile.createNewFile();
                return null;
            }
            return NBTUtils.deserializeNBTFile(playerDataFile) instanceof NBTCompound nbtCompound ? nbtCompound : null;
        }
        catch (IOException e) {
            return null;
        }
    }

}
