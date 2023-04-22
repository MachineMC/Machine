/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.utils;

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
    void write(ServerBuffer buf);

    /**
     * Serializes the object using a buffer implementation.
     * @param buf buffer implementation to use for serialization
     * @param write if the object should be written into the buffer
     * @return serialized object
     */
    default byte[] serialize(ServerBuffer buf, boolean write) {
        final ServerBuffer target = write ? buf : buf.clone();
        final int reader = target.readerIndex();
        target.setReaderIndex(target.writerIndex());
        target.write(this);
        final byte[] bytes = target.finish();
        target.setReaderIndex(reader);
        return bytes;
    }

}
