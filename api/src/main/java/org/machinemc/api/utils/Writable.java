package org.machinemc.api.utils;

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
     * @apiNote Classes that have own methods for writing in the buffer
     * shouldn't add own way of serialization, but rather call buffer's method.
     */
    void write(@NotNull ServerBuffer buf);

    /**
     * Serializes the object using a buffer implementation.
     * @param buf buffer implementation to use for serialization
     * @param write if the object should be written into the buffer
     * @return serialized object
     */
    default byte @NotNull [] serialize(@NotNull ServerBuffer buf, boolean write) {
        final ServerBuffer target = write ? buf : buf.clone();
        final int reader = target.readerIndex();
        target.setReaderIndex(target.writerIndex());
        target.write(this);
        final byte[] bytes = target.finish();
        target.setReaderIndex(reader);
        return bytes;
    }

}
