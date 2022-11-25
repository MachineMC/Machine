package me.pesekjak.machine.codegen;

import me.pesekjak.machine.codegen.blockdata.BlockDataLibGenerator;
import me.pesekjak.machine.codegen.materials.MaterialsLibGenerator;

import java.io.File;
import java.io.IOException;

public class Generators {

    public static void run(File projectDir) {
        try {
            if(projectDir.isFile()) return;
            final File outputDir = new File(projectDir.getPath() + "/libs");
            if(!outputDir.exists() && !outputDir.mkdirs())
                throw new IOException("Folder for Machine libraries could not be created");

            final CodeGenerator materials = new MaterialsLibGenerator(outputDir);
            if(!materials.isExists()) materials.generate();

            final CodeGenerator blockdata = new BlockDataLibGenerator(outputDir);
            if(!blockdata.isExists()) blockdata.generate();

        } catch (Exception exception) {
            System.out.println("Machine Library Generator unexpectedly ended.");
            exception.printStackTrace();
        }
    }

}
