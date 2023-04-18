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

    private static File machineJar;

    static {
        final URL location = Machine.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        try {
            machineJar = new File(location.toURI());
        } catch (URISyntaxException exception) {
            machineJar = new File(location.getFile());
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
     * @return jar file of the server
     */
    public static File getMachineJar() {
        return machineJar;
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
