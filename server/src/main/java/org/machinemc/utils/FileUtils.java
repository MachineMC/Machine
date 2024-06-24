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
package org.machinemc.utils;

import org.machinemc.Machine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Utilities related to operations with files.
 */
public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates new file from the file in server's resources.
     *
     * @param target file to be created or directory where the file should be
     *               created, in case directory is provided, name of the
     *               source file is used
     * @param source path to the source
     * @return true if creation was successful
     */
    public static boolean createServerFile(final File target, final String source) throws IOException {
        File targetFile = target;

        if (targetFile.isDirectory()) {
            final String[] split = source.split("/");
            targetFile = new File(targetFile, split[split.length - 1]);
        }

        final File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs())
            throw new IOException("Failed to create the parent directory of " + target.getPath());

        final InputStream in = Machine.class.getResourceAsStream(source);
        if (in == null) return false;

        Files.copy(in, targetFile.toPath());
        return true;
    }

}
