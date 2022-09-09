package me.pesekjak.machine.utils;

import me.pesekjak.machine.Machine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private static final File MACHINE_JAR = new File(Machine.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getFile());


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
            boolean s = new File(path).mkdirs();
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

}
