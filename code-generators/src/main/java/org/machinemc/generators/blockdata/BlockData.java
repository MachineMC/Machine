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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.generators.CodeGenerator;
import org.objectweb.asm.*;

import java.util.*;

import static org.machinemc.generators.CodeGenerator.*;

public final class BlockData {

    public static final String BLOCKDATA_CLASS = "org.machinemc.api.world.BlockDataImpl";
    public static final String I_BLOCKDATA_CLASS = "org.machinemc.api.world.BlockData";

    @Getter
    private final String name;

    // Linked HashMaps are important, system depends on order!
    private Set<Property> properties                    = new LinkedHashSet<>(); // used properties by this block data
    private Map<Property, List<String>> availableValues = new LinkedHashMap<>(); // available values for the properties

    private BlockDataGroup[] groups = new BlockDataGroup[0];

    private Map<Property, String> defaultState = new HashMap<>();

    private int startingState = -1;

    @Getter
    private final String path;

    private BlockData(final String id) {
        this.name = toCamelCase(id.replaceFirst("minecraft:", ""), true);
        path = "org.machinemc.api.world." + this.name + "Data";
    }

    /**
     * Creates new block data instance.
     * @param generator generator to use
     * @param name name of the block data
     * @param json json report file
     * @param groups groups of the block data
     * @return block data
     */
    public static BlockData create(final BlockDataLibGenerator generator,
                                   final String name,
                                   final JsonObject json,
                                   final @Nullable BlockDataGroup[] groups) {
        if (json.get("properties") == null) return null;

        // Linked HashMaps are important, system depends on order!
        final Set<Property> properties                    = new LinkedHashSet<>();
        final Map<Property, List<String>> availableValues = new LinkedHashMap<>();

        final Map<Property, String> defaultState = new HashMap<>();

        int startingState = -1;

        final JsonObject jsonProperties = json.get("properties").getAsJsonObject();
        for (final Map.Entry<String, JsonElement> entry : jsonProperties.entrySet()) {
            final Property property = generator.getProperties().get(entry.getKey());
            properties.add(property);
            availableValues.put(property, new ArrayList<>());
            jsonProperties.get(entry.getKey()).getAsJsonArray()
                    .forEach(value -> availableValues.get(property).add(value.getAsString().toUpperCase())
                    );
        }

        final JsonArray jsonStates = json.get("states").getAsJsonArray();
        for (final JsonElement stateElement : jsonStates) {
            final int stateID = stateElement.getAsJsonObject().get("id").getAsInt();
            if (stateID < startingState || startingState == -1)
                startingState = stateID;
            final boolean isDefault = stateElement.getAsJsonObject().get("default") != null;
            final JsonObject stateProperties = stateElement.getAsJsonObject().get("properties").getAsJsonObject();
            if (isDefault) {
                for (final Map.Entry<String, JsonElement> entry : stateProperties.entrySet())
                    defaultState.put(
                            generator.getProperties().get(entry.getKey()),
                            entry.getValue().getAsString()
                    );
            }
        }

        final BlockData blockData = new BlockData(name);

        blockData.properties      = properties;
        blockData.availableValues = availableValues;

        if (groups != null)
            blockData.groups = groups;

        blockData.defaultState = defaultState;

        blockData.startingState = startingState;
        return blockData;
    }

    /**
     * Generates the data for the block data class.
     * @return data for the block data class
     */
    public byte[] generate() {
        final ClassWriter cw = createWriter();
        final Set<String> interfaces = new LinkedHashSet<>();
        for (final Property property : properties)
            interfaces.add(type(property.getInterfacePath()).getInternalName());
        for (final BlockDataGroup group : groups)
            interfaces.add(type(group.getPath()).getInternalName());
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                type(path).getInternalName(),
                null,
                type(BLOCKDATA_CLASS).getInternalName(),
                interfaces.toArray(new String[0]));
        CodeGenerator.visitGeneratedAnnotation(cw, BlockDataLibGenerator.class);

        // Fields
        for (final Property property : properties) {
            final String descriptor = switch (property.getType()) {
                case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                case NUMBER -> Type.INT_TYPE.getDescriptor();
                case OTHER -> type(property.getPath()).getDescriptor();
            };
            final FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE,
                    toCamelCase(property.getName(), false),
                    descriptor,
                    null,
                    null);
            final AnnotationVisitor av = fv.visitAnnotation("Lorg/machinemc/api/world/PropertyRange;", true);
            final AnnotationVisitor arrayVisitor = av.visitArray("available");
            for (final String value : availableValues.get(property))
                arrayVisitor.visit(value, property.getType() == Property.Type.OTHER ? value : value.toLowerCase());
            arrayVisitor.visitEnd();
            av.visitEnd();
            fv.visitEnd();
            fv.visitEnd();
            cw.visitEnd();
            // Getter
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                    "get" + property.getFormattedName(),
                    "()" + descriptor,
                    null,
                    new String[0]);
            mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
            mv.visitEnd();
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false),
                    descriptor);
            mv.visitInsn(
                    switch (property.getType()) {
                        case BOOLEAN, NUMBER -> Opcodes.IRETURN;
                        case OTHER -> Opcodes.ARETURN;
            });
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            cw.visitEnd();
            // Setter
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                    "set" + property.getFormattedName(),
                    "(" + descriptor + ")" + type(path).getDescriptor(),
                    null,
                    new String[0]);
            mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
            mv.visitEnd();
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(
                    switch (property.getType()) {
                        case BOOLEAN, NUMBER -> Opcodes.ILOAD;
                        case OTHER -> Opcodes.ALOAD;
            }, 1);
            mv.visitFieldInsn(Opcodes.PUTFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false),
                    descriptor);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            cw.visitEnd();
        }

        // Constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                type(BLOCKDATA_CLASS).getInternalName(),
                "<init>",
                "()V",
                false);
        for (final Property property : properties) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            final String defaultValue = defaultState.get(property);
            switch (property.getType()) {
                case BOOLEAN -> pushValue(mv, Boolean.parseBoolean(defaultValue.toLowerCase()));
                case NUMBER -> pushValue(mv, Integer.parseInt(defaultValue));
                case OTHER -> mv.visitFieldInsn(Opcodes.GETSTATIC,
                        type(property.getPath()).getInternalName(),
                        defaultValue.toUpperCase(),
                        type(property.getPath()).getDescriptor());
            }
            mv.visitFieldInsn(Opcodes.PUTFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false),
                    switch (property.getType()) {
                        case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                        case NUMBER -> Type.INT_TYPE.getDescriptor();
                        case OTHER -> type(property.getPath()).getDescriptor();
                    });
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // all args constructor
        final StringBuilder descriptorBuilder = new StringBuilder().append("(");
        for (final Property property : properties)
            descriptorBuilder.append(
                    switch (property.getType()) {
                        case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                        case NUMBER -> Type.INT_TYPE.getDescriptor();
                        case OTHER -> type(property.getPath()).getDescriptor();
            });
        descriptorBuilder.append(")V");
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                descriptorBuilder.toString(),
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                type(BLOCKDATA_CLASS).getInternalName(),
                "<init>",
                "()V",
                false);
        int i = 1;
        for (final Property property : properties) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(
                    switch (property.getType()) {
                        case BOOLEAN, NUMBER -> Opcodes.ILOAD;
                        case OTHER -> Opcodes.ALOAD;
            }, i);
            mv.visitFieldInsn(Opcodes.PUTFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false),
                    switch (property.getType()) {
                        case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                        case NUMBER -> Type.INT_TYPE.getDescriptor();
                        case OTHER -> type(property.getPath()).getDescriptor();
                    });
            i++;
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // getData method
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
                "getData",
                "()[Ljava/lang/Object;",
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        pushValue(mv, properties.size());
        mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getType(Object.class).getInternalName());
        i = 0;
        for (final Property property : properties) {
            mv.visitInsn(Opcodes.DUP);
            pushValue(mv, i);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false),
                    switch (property.getType()) {
                        case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                        case NUMBER -> Type.INT_TYPE.getDescriptor();
                        case OTHER -> type(property.getPath()).getDescriptor();
                    });
            if (property.getType() == Property.Type.BOOLEAN)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        Type.getType(Boolean.class).getInternalName(),
                        "valueOf",
                        "(Z)Ljava/lang/Boolean;",
                        false);
            else if (property.getType() == Property.Type.NUMBER)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        Type.getType(Integer.class).getInternalName(),
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false);
            mv.visitInsn(Opcodes.AASTORE);
            i++;
        }
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // getDataNames method
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
                "getDataNames",
                "()[Ljava/lang/String;",
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        pushValue(mv, properties.size());
        mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getType(String.class).getInternalName());
        i = 0;
        for (final Property property : properties) {
            mv.visitInsn(Opcodes.DUP);
            pushValue(mv, i);
            pushValue(mv, property.getName());
            mv.visitInsn(Opcodes.AASTORE);
            i++;
        }
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // getAcceptedProperties method
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
                "getAcceptedProperties",
                "()[[Ljava/lang/Object;",
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        pushValue(mv, properties.size());
        mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getType(Object[].class).getInternalName());
        i = 0;
        for (final Property property : properties) {
            mv.visitInsn(Opcodes.DUP);
            pushValue(mv, i);

            final List<String> available = availableValues.get(property);
            pushValue(mv, available.size());
            mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getType(Object.class).getInternalName());
            for (int j = 0; j < available.size(); j++) {
                final String value = available.get(j);
                mv.visitInsn(Opcodes.DUP);
                pushValue(mv, j);
                switch (property.getType()) {
                    case BOOLEAN -> {
                        pushValue(mv, Boolean.parseBoolean(value));
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                Type.getType(Boolean.class).getInternalName(),
                                "valueOf",
                                "(Z)Ljava/lang/Boolean;",
                                false);
                    }
                    case NUMBER -> {
                        pushValue(mv, Integer.parseInt(value));
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                Type.getType(Integer.class).getInternalName(),
                                "valueOf",
                                "(I)Ljava/lang/Integer;",
                                false);
                    }
                    case OTHER -> mv.visitFieldInsn(Opcodes.GETSTATIC,
                            type(property.getPath()).getInternalName(),
                            value,
                            type(property.getPath()).getDescriptor());
                }
                mv.visitInsn(Opcodes.AASTORE);
            }

            mv.visitInsn(Opcodes.AASTORE);
            i++;
        }
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // firstStateID method
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
                "firstStateID",
                "()I",
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        pushValue(mv, startingState);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // loadProperties method
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
                "loadProperties",
                "([Ljava/lang/Object;)V",
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();

        int j = 0;
        for (final Property property : properties) {

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            pushValue(mv, j);
            mv.visitInsn(Opcodes.AALOAD);
            mv.visitTypeInsn(Opcodes.CHECKCAST, (
                    switch (property.getType()) {
                        case BOOLEAN -> Type.getType(Boolean.class);
                        case NUMBER -> Type.getType(Integer.class);
                        case OTHER -> type(property.getPath()); }
            ).getInternalName());

            if (property.getType() == Property.Type.BOOLEAN)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        Type.getType(Boolean.class).getInternalName(),
                        "booleanValue",
                        "()Z",
                        false);
            else if (property.getType() == Property.Type.NUMBER)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        Type.getType(Integer.class).getInternalName(),
                        "intValue",
                        "()I",
                        false);

            mv.visitFieldInsn(Opcodes.PUTFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false), (
                            switch (property.getType()) {
                                case BOOLEAN -> Type.BOOLEAN_TYPE;
                                case NUMBER -> Type.INT_TYPE;
                                case OTHER -> type(property.getPath()); }
                    ).getDescriptor());

            j++;
        }

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

}
