package me.pesekjak.machine.utils;

import org.jglrxavpok.hephaistos.nbt.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class NBTUtils {

    public static NBTList<NBTDouble> doubleList(double... doubles) {
        return new NBTList<>(NBTType.TAG_Double, Arrays.stream(doubles)
                .mapToObj(NBTDouble::new)
                .toList());
    }

    public static NBTList<NBTFloat> floatList(float... floats) {
        // This is super weird, you can't stream an array of floats??????
        Float[] floatArray = new Float[floats.length];
        for (int i = 0; i < floats.length; i++)
            floatArray[i] = floats[i];
        return new NBTList<>(NBTType.TAG_Float, Arrays.stream(floatArray)
                .map(NBTFloat::new)
                .toList());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void serializeNBT(File file, NBT nbt) {
        try {
            if (!file.exists()) {
                if (file.getParentFile() != null)
                    file.getParentFile().mkdirs();
                if (!file.createNewFile())
                    throw new IOException("Unable to create file at " + file.getAbsolutePath());
            }
            try (NBTWriter writer = new NBTWriter(file, CompressedProcesser.GZIP)) {
                writer.writeNamed("", nbt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NBT deserializeNBTFile(File file) {
        try (NBTReader reader = new NBTReader(file, CompressedProcesser.GZIP)) {
            return reader.read();
        } catch (IOException | NBTException e) {
            throw new RuntimeException("Couldn't read file at " + file.getAbsolutePath(), e);
        }
    }

}
