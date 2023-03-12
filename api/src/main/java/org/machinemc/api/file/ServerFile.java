package org.machinemc.api.file;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

/**
 * Represents a file of the server.
 */
public interface ServerFile {

    /**
     * @return name of the file
     */
    String getName();

    /**
     * @return stream of the file from the resources if
     * default version exists.
     */
    @Nullable InputStream getOriginal();

}
