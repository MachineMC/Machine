package me.pesekjak.machine.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Represents objects that are serializable and can
 * be written to the server buffer.
 */
@FunctionalInterface
public interface Writable {

    /**
     * Writes this to the given buffer.
     * @param buf buffer to write into
     */
    void write(@NotNull ServerBuffer buf);

}
