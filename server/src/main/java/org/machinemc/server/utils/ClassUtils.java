package org.machinemc.server.utils;

import lombok.Cleanup;
import org.machinemc.server.Machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility class for operations related to classes.
 */
public final class ClassUtils {

    private ClassUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads a class.
     * @param classObject class to load
     */
    public static void loadClass(Class<?> classObject) {
        try {
            Class.forName(classObject.getName(), true, Machine.CLASS_LOADER);
        } catch (ClassNotFoundException ignored) { }
    }

    /**
     * Loads multiple classes from given package.
     * @param basePackage base package of the classes
     * @throws IOException if jar is invalid
     */
    public static void loadClasses(String basePackage) throws IOException {
        List<String> classNames = getClasses(basePackage);
        for (String className : classNames) {
            try {
                Class.forName(className, true, Machine.CLASS_LOADER);
            } catch (ClassNotFoundException | ExceptionInInitializerError ignored) { }
        }
    }

    /**
     * Returns list of class names inside of a package.
     * @param basePackage base package of the classes
     * @return list of the class inside
     * @throws IOException if jar is invalid
     */
    public static List<String> getClasses(String basePackage) throws IOException {
        @Cleanup JarFile jar = new JarFile(FileUtils.getMachineJar());
        basePackage = basePackage.replace('.', '/') + "/";
        List<String> classNames = new ArrayList<>();
        for (Iterator<JarEntry> entries = jar.entries().asIterator(); entries.hasNext(); ) {
            JarEntry entry = entries.next();
            if (entry.getName().startsWith(basePackage) && entry.getName().endsWith(".class"))
                classNames.add(entry.getName().replace('/', '.').substring(0, entry.getName().length() - ".class".length()));
        }
        return classNames;
    }

}
