package me.pesekjak.machine.utils;

import me.pesekjak.machine.Machine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class FileUtils {

    private static final File MACHINE_JAR = new File(Machine.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getFile()
            .replaceAll("%20", " "));


    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates file from resources at same location.
     * @param file File to create
     * @return true if creation was successful
     */
    public static boolean createFromDefault(final File file) {
        return createFromDefaultAndLocate(file, file.getPath());
    }

    /**
     * Creates file from resources and relocates.
     * @param file File to create
     * @param path new path for the file
     * @return true if creation was successful
     */
    public static boolean createFromDefaultAndLocate(final File file, String path) {
        if(path.endsWith("/")) {
            new File(path).mkdirs();
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
     * @return Jar file of the server
     */
    public static File getMachineJar() {
        return MACHINE_JAR;
    }

    public static UUID getOrCreateUUID(File folder) {
        File uidFile = new File(folder, "uid.dat");
        if (uidFile.exists()) {
            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(uidFile))) {
                return new UUID(dataInputStream.readLong(), dataInputStream.readLong());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        UUID uuid = UUID.randomUUID();
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(uidFile))) {
            dataOutputStream.writeLong(uuid.getMostSignificantBits());
            dataOutputStream.writeLong(uuid.getLeastSignificantBits());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return uuid;
    }

}
