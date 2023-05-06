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

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.apache.tools.ant.filters.StringInputStream;
import org.machinemc.generators.blockdata.BlockDataLibGenerator;
import org.machinemc.generators.materials.MaterialsLibGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RequiredArgsConstructor
public final class Generators {

    private final File projectDir;

    private JsonObject versions;
    private JsonObject userVersions;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Runs the generators in a provided directory.
     * @param projectDir directory to run the generators in
     */
    public static void run(final File projectDir) {
        try {
            new Generators(projectDir).run0();
        } catch (Throwable throwable) {
            System.out.println("Machine Library Generator unexpectedly ended.");
            throw new RuntimeException(throwable);
        }
    }

    private void run0() throws Throwable {
        if (projectDir.isFile()) return;
        final File outputDir = new File(projectDir.getPath() + "/libs");
        if (!outputDir.exists() && !outputDir.mkdirs())
            throw new IOException("Folder for Machine libraries could not be created");

        final File versionsFile = new File(outputDir, "versions.json");
        final InputStream is = getClass().getClassLoader().getResourceAsStream("versions.json");
        final byte[] data;
        try (is) {
            if (is == null) throw new NullPointerException();
            data = is.readAllBytes();
        }

        versions = new JsonParser().parse(
                new InputStreamReader(new ByteArrayInputStream(data))
        ).getAsJsonObject();

        final boolean regenerate;

        if (!versionsFile.exists()) {
            if (!versionsFile.createNewFile())
                throw new IOException("Versions file for Machine libraries could not be created");
            Files.copy(new ByteArrayInputStream(data), versionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            regenerate = true;
        } else {
            regenerate = false;
        }

        final InputStreamReader reader = new InputStreamReader(new FileInputStream(versionsFile));
        try (reader) {
            userVersions = new JsonParser().parse(reader).getAsJsonObject();
        }

        handle(new MaterialsLibGenerator(outputDir), regenerate);
        handle(new BlockDataLibGenerator(outputDir), regenerate);

        Files.copy(new StringInputStream(gson.toJson(userVersions)),
                versionsFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    private void handle(final CodeGenerator generator, final boolean regenerate) throws Throwable {
        if (!generator.getJar().exists()) {
            generator.load();
            generator.generate();
            return;
        }
        final String version = versions.get(generator.getLibraryName()).getAsString();
        final JsonElement element = userVersions.get(generator.getLibraryName());
        final String userVersion = element != null ? element.getAsString() : null;
        if (!version.equals(userVersion) || regenerate) {
            if (!generator.getJar().delete() || !generator.getJar().createNewFile())
                throw new IOException(generator.getLibraryName() + " Machine library could not be recreated");
            generator.load();
            generator.generate();
            userVersions.addProperty(generator.getLibraryName(), version);
        }
    }

}
