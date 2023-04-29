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
import org.machinemc.generators.CodeGenerator;
import org.objectweb.asm.*;

import java.util.*;

import static org.machinemc.generators.CodeGenerator.*;

public final class BlockData {

    public static final String BLOCKDATA_CLASS = "org.machinemc.api.world.BlockDataImpl";
    public static final String I_BLOCKDATA_CLASS = "org.machinemc.api.world.BlockData";

    @Getter
    private final String name;
    private final String id;

    // Linked HashMaps are important, system depends on order!
    private Set<Property> properties                    = new LinkedHashSet<>();
    private Map<Property, List<String>> availableValues = new LinkedHashMap<>();
    private Map<String, Integer> idMap                  = new LinkedHashMap<>();

    private Map<Integer, List<Map.Entry<Property, String>>> blockDataMap = new LinkedHashMap<>();

    private Map<Property, String> defaultState = new HashMap<>();

    @Getter
    private final String path;

    private BlockData(final String id) {
        this.name = toCamelCase(id.replaceFirst("minecraft:", ""), true);
        this.id = id;
        path = "org.machinemc.api.world." + this.name + "Data";
    }

    /**
     * Creates new block data instance.
     * @param generator generator to use
     * @param name name of the block data
     * @param json json report file
     * @return block data
     */
    public static BlockData create(final BlockDataLibGenerator generator, final String name, final JsonObject json) {
        if (json.get("properties") == null) return null;

        // Linked HashMaps are important, system depends on order!
        final Set<Property> properties                    = new LinkedHashSet<>();
        final Map<Property, List<String>> availableValues = new LinkedHashMap<>();
        final Map<String, Integer> idMap                  = new LinkedHashMap<>();

        final Map<Integer, List<Map.Entry<Property, String>>> blockDataMap = new LinkedHashMap<>();

        final Map<Property, String> defaultState = new HashMap<>();

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
            final int stateId = stateElement.getAsJsonObject().get("id").getAsInt();
            blockDataMap.put(stateId, new ArrayList<>());
            final boolean isDefault = stateElement.getAsJsonObject().get("default") != null;
            final JsonObject stateProperties = stateElement.getAsJsonObject().get("properties").getAsJsonObject();
            final StringBuilder key = new StringBuilder();
            for (final Map.Entry<String, JsonElement> entry : stateProperties.entrySet()) {
                key.append(entry.getValue().getAsString());
                key.append(";");
                final Property property = generator.getProperties().get(entry.getKey());
                blockDataMap.get(stateId).add(new AbstractMap.SimpleEntry<>(property, entry.getValue().getAsString()));
                if (isDefault)
                    defaultState.put(property, entry.getValue().getAsString());
            }
            idMap.put(key.toString().toLowerCase(), stateId);
        }

        final BlockData blockData = new BlockData(name);

        blockData.properties      = properties;
        blockData.availableValues = availableValues;
        blockData.idMap           = idMap;

        blockData.blockDataMap = blockDataMap;

        blockData.defaultState = defaultState;
        return blockData;
    }

    /**
     * Generates the data for the block data class.
     * @return data for the block data class
     */
    public byte[] generate() {
        final ClassWriter cw = new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        final Set<String> interfaces = new LinkedHashSet<>();
        for (final Property property : properties)
            interfaces.add(type(property.getInterfacePath()).getInternalName());
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC,
                type(path).getInternalName(),
                null,
                type(BLOCKDATA_CLASS).getInternalName(),
                interfaces.toArray(new String[0]));
        CodeGenerator.visitGeneratedAnnotation(cw, BlockDataLibGenerator.class);

        // Fields
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                "ID_MAP",
                Type.getType(HashMap.class).getDescriptor(),
                "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Integer;>;",
                null);
        fv.visitEnd();
        fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                "BLOCKDATA_MAP",
                Type.getType(HashMap.class).getDescriptor(),
                "Ljava/util/HashMap<Ljava/lang/Integer;" + type(path).getDescriptor() + ">;",
                null);
        fv.visitEnd();
        for (final Property property : properties) {
            final String descriptor = switch (property.getType()) {
                case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                case NUMBER -> Type.INT_TYPE.getDescriptor();
                case OTHER -> type(property.getPath()).getDescriptor();
            };
            fv = cw.visitField(Opcodes.ACC_PRIVATE,
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
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
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
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
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

        // Static block
        mv = cw.visitMethod(Opcodes.ACC_STATIC,
                "<clinit>",
                "()V",
                null,
                new String[0]);
        mv.visitCode();
        i = 0;
        for (final String fieldName : List.of("ID_MAP", "BLOCKDATA_MAP")) {
            mv.visitTypeInsn(Opcodes.NEW, Type.getType(HashMap.class).getInternalName());
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    Type.getType(HashMap.class).getInternalName(),
                    "<init>",
                    "()V",
                    false);
            mv.visitVarInsn(Opcodes.ASTORE, i);
            mv.visitVarInsn(Opcodes.ALOAD, i);
            mv.visitFieldInsn(Opcodes.PUTSTATIC,
                    type(path).getInternalName(),
                    fieldName,
                    Type.getType(HashMap.class).getDescriptor());
            i++;
        }
        for (final String key : idMap.keySet()) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            pushValue(mv, key);
            pushValue(mv, idMap.get(key));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getType(Integer.class).getInternalName(),
                    "valueOf",
                    "(I)Ljava/lang/Integer;",
                    false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    Type.getType(HashMap.class).getInternalName(),
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false);
            mv.visitInsn(Opcodes.POP);
        }
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                type(path).getInternalName(),
                "initBlockDataMap",
                "()V",
                false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // Init block data map method
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED | Opcodes.ACC_STATIC,
                "initBlockDataMap",
                "()V",
                null,
                new String[0]);
        mv.visitCode();
        for (final Integer id : blockDataMap.keySet()) {
            mv.visitFieldInsn(Opcodes.GETSTATIC,
                    type(path).getInternalName(),
                    "BLOCKDATA_MAP",
                    Type.getType(HashMap.class).getDescriptor());
            final StringBuilder stateKeyBuilder = new StringBuilder();
            for (final Map.Entry<Property, String> property : blockDataMap.get(id))
                stateKeyBuilder.append(property.getValue().toLowerCase()).append(";");
            pushValue(mv, idMap.get(stateKeyBuilder.toString()));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getType(Integer.class).getInternalName(),
                    "valueOf",
                    "(I)Ljava/lang/Integer;",
                    false);
            mv.visitTypeInsn(Opcodes.NEW, type(path).getInternalName());
            mv.visitInsn(Opcodes.DUP);
            for (final Map.Entry<Property, String> property : blockDataMap.get(id)) {
                switch (property.getKey().getType()) {
                    case BOOLEAN -> pushValue(mv, Boolean.parseBoolean(property.getValue()));
                    case NUMBER -> pushValue(mv, Integer.parseInt(property.getValue()));
                    case OTHER -> mv.visitFieldInsn(Opcodes.GETSTATIC,
                            type(property.getKey().getPath()).getInternalName(),
                            property.getValue().toUpperCase(),
                            type(property.getKey().getPath()).getDescriptor());
                }
            }
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    type(path).getInternalName(),
                    "<init>",
                    descriptorBuilder.toString(),
                    false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    Type.getType(HashMap.class).getInternalName(),
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false);
            mv.visitInsn(Opcodes.POP);
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

        mv = cw.visitMethod(Opcodes.ACC_PROTECTED,
                "getIdMap",
                "()" + Type.getType(Map.class).getDescriptor(),
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                type(path).getInternalName(),
                "BLOCKDATA_MAP",
                Type.getType(HashMap.class).getDescriptor());
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // getId method
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "getId",
                "()I",
                null,
                new String[0]);
        mv.visitAnnotation(Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                type(path).getInternalName(),
                "ID_MAP",
                Type.getType(HashMap.class).getDescriptor());
        mv.visitTypeInsn(Opcodes.NEW, Type.getType(StringBuilder.class).getInternalName());
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getType(StringBuilder.class).getInternalName(),
                "<init>",
                "()V",
                false);
        for (final Property property : properties) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD,
                    type(path).getInternalName(),
                    toCamelCase(property.getName(), false),
                    switch (property.getType()) {
                        case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                        case NUMBER -> Type.INT_TYPE.getDescriptor();
                        case OTHER -> type(property.getPath()).getDescriptor();
                    });
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    Type.getType(StringBuilder.class).getInternalName(),
                    "append",
                    "(" + switch (property.getType()) {
                        case BOOLEAN -> Type.BOOLEAN_TYPE.getDescriptor();
                        case NUMBER -> Type.INT_TYPE.getDescriptor();
                        case OTHER -> Type.getType(Object.class).getDescriptor();
                    } + ")Ljava/lang/StringBuilder;",
                    false);
            pushValue(mv, ";");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    Type.getType(StringBuilder.class).getInternalName(),
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                    false);
        }
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(StringBuilder.class).getInternalName(),
                "toString",
                "()Ljava/lang/String;",
                false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(String.class).getInternalName(),
                "toLowerCase",
                "()Ljava/lang/String;",
                false);
        mv.visitLdcInsn(0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                Type.getType(Integer.class).getInternalName(),
                "valueOf",
                "(I)Ljava/lang/Integer;",
                false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(HashMap.class).getInternalName(),
                "getOrDefault",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getType(Integer.class).getInternalName());
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(Integer.class).getInternalName(),
                "intValue",
                "()I",
                false);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

}
