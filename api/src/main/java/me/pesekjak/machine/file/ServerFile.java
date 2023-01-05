package me.pesekjak.machine.file;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

/**
 * Represents a file of the server.
 */
public interface ServerFile {

    /**
     * @return name of the file
     */
    @NotNull @NonNls String getName();

    /**
     * @return stream of the file from the resources if
     * default version exists.
     */
    @Nullable InputStream getOriginal();

}
