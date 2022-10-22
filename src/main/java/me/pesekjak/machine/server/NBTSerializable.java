package me.pesekjak.machine.server;

import me.pesekjak.machine.utils.NBTUtils;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.File;

public interface NBTSerializable {

    NBTCompound toNBT();

    default void serializeNBT(File file) {
        NBTUtils.serializeNBT(file, toNBT());
    }

}
