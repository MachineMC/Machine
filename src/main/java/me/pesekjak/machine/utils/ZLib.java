package me.pesekjak.machine.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public final class ZLib {

    private ZLib() {
        throw new UnsupportedOperationException();
    }

    /**
     * Compresses an array of bytes using Zlib.
     * @param data The array of bytes to compress
     * @return The compressed bytes
     */
    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DeflaterOutputStream outputStream = new DeflaterOutputStream(bytes);
        outputStream.write(data);
        outputStream.finish();
        return bytes.toByteArray();
    }

    /**
     * Decompresses a compressed array of bytes.
     * @param data The compressed bytes
     * @return The decompressed bytes
     */
    public static byte[] decompress(byte[] data) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        InflaterOutputStream outputStream = new InflaterOutputStream(bytes);
        outputStream.write(data);
        outputStream.finish();
        return bytes.toByteArray();
    }

}
