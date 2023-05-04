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
package org.machinemc.generators;

import org.machinemc.generators.blockdata.BlockDataLibGenerator;
import org.machinemc.generators.materials.MaterialsLibGenerator;

import java.io.File;
import java.io.IOException;

public final class Generators {

    private Generators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the generators in a provided directory.
     * @param projectDir directory to run the generators in
     */
    public static void run(final File projectDir) {
        try {
            run0(projectDir);
        } catch (Throwable throwable) {
            System.out.println("Machine Library Generator unexpectedly ended.");
            throw new RuntimeException(throwable);
        }
    }

    private static void run0(final File projectDir) throws Throwable {
        if (projectDir.isFile()) return;
        final File outputDir = new File(projectDir.getPath() + "/libs");
        if (!outputDir.exists() && !outputDir.mkdirs())
            throw new IOException("Folder for Machine libraries could not be created");

        final CodeGenerator materials = new MaterialsLibGenerator(outputDir);
        if (!materials.isExists()) materials.generate();

        final CodeGenerator blockdata = new BlockDataLibGenerator(outputDir);
        if (!blockdata.isExists()) blockdata.generate();
    }

}
