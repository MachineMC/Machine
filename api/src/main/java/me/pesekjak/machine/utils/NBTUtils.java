package me.pesekjak.machine.utils;

import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import mx.kenzie.nbt.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class for NBT-related operations.
 */
@UtilityClass
public class NBTUtils {

    /**
     * Creates an NBT double list from doubles.
     * @param doubles The doubles to convert
     * @return NBT double list
     */
    @Contract("_ -> new")
    public static @NotNull NBTList doubleList(double @NotNull ... doubles) {
        return new NBTList(Arrays.stream(doubles).boxed().collect(Collectors.toList()));
    }

    /**
     * Creates an NBT float list from floats.
     * @param floats The floats to convert
     * @return NBT float list
     */
    @Contract("_ -> new")
    public static @NotNull NBTList floatList(float @NotNull ... floats) {
        Float[] floatArray = new Float[floats.length];
        for (int i = 0; i < floats.length; i++)
            floatArray[i] = floats[i];
        return new NBTList(Arrays.stream(floatArray).toList());
    }

    /**
     * Serializes NBT to a file (creates the file if it doesn't exist).
     * @param file The file to serialize to
     * @param nbt The NBT to serialize
     */
    @Synchronized
    public static void serializeNBT(@NotNull File file, @NotNull NBTCompound nbt) {
        try {
            if (!file.exists() && !file.createNewFile())
                throw new IOException("Unable to create file at " + file.getAbsolutePath());
            nbt.write(file);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns NBT from a file.
     * @param file The file to deserialize from
     * @return The NBT stored in the file
     */
    @Synchronized
    public static @NotNull NBTCompound deserializeNBTFile(@NotNull File file) {
        try {
            NBTCompound compound = new NBTCompound();
            compound.read(file);
            return compound;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file at " + file.getAbsolutePath(), e);
        }
    }

}
