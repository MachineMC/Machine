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
package org.machinemc.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * Utility class used for ZLib compression.
 */
public final class ZLib {

    private ZLib() {
        throw new UnsupportedOperationException();
    }

    /**
     * Compresses an array of bytes using ZLib.
     * @param data The array of bytes to compress
     * @return The compressed bytes
     * @throws IOException if an I/O error occurs
     */
    public static byte[] compress(final byte[] data) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DeflaterOutputStream outputStream = new DeflaterOutputStream(bytes);
        outputStream.write(data);
        outputStream.finish();
        return bytes.toByteArray();
    }

    /**
     * Decompresses a compressed array of bytes.
     * @param data The compressed bytes
     * @return The decompressed bytes
     * @throws IOException if an I/O error occurs
     */
    public static byte[] decompress(final byte[] data) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final InflaterOutputStream outputStream = new InflaterOutputStream(bytes);
        outputStream.write(data);
        outputStream.finish();
        return bytes.toByteArray();
    }

}
