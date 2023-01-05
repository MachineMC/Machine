package me.pesekjak.machine.utils;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import me.pesekjak.machine.Machine;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Class for file related operations.
 */
@UtilityClass
public class FileUtils {

    private static @NotNull File MACHINE_JAR;

    static {
        final URL location = Machine.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        try {
            MACHINE_JAR = new File(location.toURI());
        } catch (URISyntaxException exception) {
            MACHINE_JAR = new File(location.getFile());
        }
    }

    /**
     * Creates file from resources at same location.
     * @param file file to create
     * @return true if creation was successful
     */
    public static boolean createFromDefault(@NotNull File file) {
        return createFromDefaultAndLocate(file, file.getPath());
    }

    /**
     * Creates file from resources and relocates.
     * @param file file to create
     * @param path new path for the file
     * @return true if creation was successful
     */
    public static boolean createFromDefaultAndLocate(@NotNull File file, @NotNull String path) {
        if(path.endsWith("/")) {
            final File pathFile = new File(path);
            if(!pathFile.mkdirs() && !pathFile.exists())
                return false;
            path = path + file.getName();
        }
        InputStream in = Machine.CLASS_LOADER.getResourceAsStream(file.getPath());
        if(in == null) return false;
        try {
            Files.copy(in, Path.of(path));
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    /**
     * @return jar file of the server
     */
    public static @NotNull File getMachineJar() {
        return MACHINE_JAR;
    }

    /**
     * Creates uid data file at the given folder
     * @param folder folder to create uid file at
     * @return uuid saved in the file
     */
    public static @NotNull UUID getOrCreateUUID(@NotNull File folder) {
        final File uidFile = new File(folder, "uid.dat");
        if (uidFile.exists()) {
            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(uidFile))) {
                return new UUID(dataInputStream.readLong(), dataInputStream.readLong());
            }
            catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        UUID uuid = UUID.randomUUID();
        try {
            if(!uidFile.exists() && !uidFile.createNewFile())
                throw new IllegalStateException();
            @Cleanup DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(uidFile));
            dataOutputStream.writeLong(uuid.getMostSignificantBits());
            dataOutputStream.writeLong(uuid.getLeastSignificantBits());
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return uuid;
    }

}
