package me.pesekjak.machine.utils;

import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
    public static @NotNull NBTList<NBTDouble> doubleList(double @NotNull ... doubles) {
        return new NBTList<>(NBTType.TAG_Double, Arrays.stream(doubles)
                .mapToObj(NBTDouble::new)
                .toList());
    }

    /**
     * Creates an NBT float list from floats.
     * @param floats The floats to convert
     * @return NBT float list
     */
    @Contract("_ -> new")
    public static @NotNull NBTList<NBTFloat> floatList(float @NotNull ... floats) {
        Float[] floatArray = new Float[floats.length];
        for (int i = 0; i < floats.length; i++)
            floatArray[i] = floats[i];
        return new NBTList<>(NBTType.TAG_Float, Arrays.stream(floatArray)
                .map(NBTFloat::new)
                .toList());
    }

    /**
     * Serializes NBT to a file (creates the file if it doesn't exist).
     * @param file The file to serialize to
     * @param nbt The NBT to serialize
     */
    @Synchronized
    public static void serializeNBT(@NotNull File file, @NotNull NBT nbt) {
        try {
            if (!file.exists() && !file.createNewFile())
                throw new IOException("Unable to create file at " + file.getAbsolutePath());
            try (NBTWriter writer = new NBTWriter(file, CompressedProcesser.NONE)) {
                writer.writeNamed("", nbt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns NBT from a file.
     * @param file The file to deserialize from
     * @return The NBT stored in the file
     */
    @Synchronized
    public static @NotNull NBT deserializeNBTFile(@NotNull File file) {
        try (NBTReader reader = new NBTReader(file, CompressedProcesser.NONE)) {
            try {
                return reader.read();
            } catch (NBTException e) {
                throw new RuntimeException("File at " + file.getAbsolutePath() + " doesn't follow nbt format");
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file at " + file.getAbsolutePath(), e);
        }
    }

}
