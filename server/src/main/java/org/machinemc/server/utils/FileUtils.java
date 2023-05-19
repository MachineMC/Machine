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
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.utils;

import lombok.Cleanup;
import org.machinemc.server.Machine;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Class for file related operations.
 */
public final class FileUtils {

    private static File sourceLocation;

    static {
        final URL location = Machine.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        try {
            sourceLocation = new File(location.toURI());
        } catch (URISyntaxException exception) {
            sourceLocation = new File(location.getFile());
        }
    }

    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates file from resources at same location.
     * @param file file to create
     * @return true if creation was successful
     */
    public static boolean createFromDefault(final File file) {
        return createFromDefaultAndLocate(file, file.getPath());
    }

    /**
     * Creates file from resources and relocates.
     * @param file file to create
     * @param path new path for the file
     * @return true if creation was successful
     */
    public static boolean createFromDefaultAndLocate(final File file, final String path) {
        String fullPath = path;
        if (fullPath.endsWith("/")) {
            final File pathFile = new File(fullPath);
            if (!pathFile.mkdirs() && !pathFile.exists())
                return false;
            fullPath = fullPath + file.getName();
        }
        final InputStream in = Machine.CLASS_LOADER.getResourceAsStream(file.getPath());
        if (in == null) return false;
        try {
            Files.copy(in, Path.of(fullPath));
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    /**
     * Creates new file from the file in server's resources.
     * @param target file to be created or directory where the file should be
     *               created, in case directory is provided, name of the
     *               source file is used
     * @param source path to the source
     * @return true if creation was successful
     */
    public static boolean createServerFile(final File target, final String source) {
        final File sourceFile = new File(source);
        File targetFile = target;

        if (targetFile.isDirectory())
            targetFile = new File(targetFile, sourceFile.getName());

        final File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs())
            throw new RuntimeException();

        final InputStream in = Machine.CLASS_LOADER.getResourceAsStream(sourceFile.getPath());
        if (in == null) return false;

        try {
            Files.copy(in, targetFile.toPath());
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    /**
     * @return location of loaded class files
     */
    public static File getSourceLocation() {
        return sourceLocation;
    }

    /**
     * Creates uid data file at the given folder.
     * @param folder folder to create uid file at
     * @return uuid saved in the file
     */
    public static UUID getOrCreateUUID(final File folder) {
        final File uidFile = new File(folder, "uid.dat");
        if (uidFile.exists()) {
            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(uidFile))) {
                return new UUID(dataInputStream.readLong(), dataInputStream.readLong());
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        final UUID uuid = UUID.randomUUID();
        try {
            if (!uidFile.exists() && !uidFile.createNewFile())
                throw new IllegalStateException();
            final @Cleanup DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(uidFile));
            dataOutputStream.writeLong(uuid.getMostSignificantBits());
            dataOutputStream.writeLong(uuid.getLeastSignificantBits());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return uuid;
    }

}
