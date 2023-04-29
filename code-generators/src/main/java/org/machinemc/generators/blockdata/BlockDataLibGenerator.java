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
package org.machinemc.generators.blockdata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import org.machinemc.generators.CodeGenerator;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockDataLibGenerator extends CodeGenerator {

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Property> properties = new LinkedHashMap<>();

    public BlockDataLibGenerator(final File outputDir) throws IOException {
        super(outputDir, "blockdata", "blocks.json");
    }

    @Override
    public void generate() throws IOException {
        System.out.println("Generating the " + super.getLibraryName() + " library");

        final ClassWriter cw = new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE,
                type("org.machinemc.api.world.blockdata.BlockDataProperty").getInternalName(),
                null,
                org.objectweb.asm.Type.getInternalName(Object.class),
                new String[0]);
        CodeGenerator.visitGeneratedAnnotation(cw, BlockDataLibGenerator.class);
        final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT,
                "getName",
                "()" + Type.getType(String.class).getDescriptor(),
                null,
                new String[0]);
        mv.visitEnd();
        cw.visitEnd();
        addClass("org.machinemc.api.world.blockdata.BlockDataProperty", cw.toByteArray());

        final JsonObject json = getSource().getAsJsonObject();
        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getValue().getAsJsonObject().get("properties") == null) continue;
            final JsonObject properties = entry.getValue().getAsJsonObject().get("properties").getAsJsonObject();
            for (final Map.Entry<String, JsonElement> propertyEntry : properties.entrySet()) {
                final Property property = new Property(propertyEntry.getKey());
                for (final JsonElement propertyValue : propertyEntry.getValue().getAsJsonArray())
                    property.addValue(propertyValue.getAsString());
                if (this.properties.get(property.getName()) != null) {
                    this.properties.get(property.getName()).merge(property);
                    continue;
                }
                this.properties.put(property.getName(), property);
            }
        }
        System.out.println("Loaded " + properties.keySet().size() + " blockdata properties");
        System.out.println("Generating the property and property interface classes...");
        for (final Property property : properties.values()) {
            addClass(property.getInterfacePath(), property.generateInterface());
            if (property.getType() != Property.Type.OTHER) continue;
            addClass(property.getPath(), property.generate());
        }
        System.out.println("Loading and generating individual block classes...");
        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            final BlockData blockData = BlockData.create(this, entry.getKey(), entry.getValue().getAsJsonObject());
            if (blockData == null) continue;
            addClass(blockData.getPath(), blockData.generate());
        }
        super.generate();
    }

}
