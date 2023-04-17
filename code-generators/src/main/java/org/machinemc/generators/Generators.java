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
            if (projectDir.isFile()) return;
            final File outputDir = new File(projectDir.getPath() + "/libs");
            if (!outputDir.exists() && !outputDir.mkdirs())
                throw new IOException("Folder for Machine libraries could not be created");

            final CodeGenerator materials = new MaterialsLibGenerator(outputDir);
            if (!materials.isExists()) materials.generate();

            final CodeGenerator blockdata = new BlockDataLibGenerator(outputDir);
            if (!blockdata.isExists()) blockdata.generate();

        } catch (Exception exception) {
            System.out.println("Machine Library Generator unexpectedly ended.");
            exception.printStackTrace();
        }
    }

}
