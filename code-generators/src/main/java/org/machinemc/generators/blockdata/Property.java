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

import lombok.Getter;
import org.machinemc.generators.CodeGenerator;
import org.objectweb.asm.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.machinemc.generators.CodeGenerator.*;

public class Property {

    @Getter
    private final String name;
    @Getter
    private final String formattedName;
    @Getter
    private final String path;
    @Getter
    private final Set<String> values = new LinkedHashSet<>();
    private Type type;

    public Property(final String name) {
        this.name = name;
        this.formattedName = toCamelCase(name, true);
        path = "org.machinemc.api.world.blockdata." + formattedName + "Property";
    }

    /**
     * Adds new value to the property.
     * @param value new value
     */
    public void addValue(final String value) {
        type = null;
        values.add(value);
    }

    /**
     * Merges (adds values) other property with this property.
     * @param other other property to merge
     */
    public void merge(final Property other) {
        for (final String value : other.values)
            addValue(value);
    }

    /**
     * Generates the data for enum class for this property.
     * @return enum class data of this property
     */
    public byte[] generate() {
        final ClassWriter cw = new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER | Opcodes.ACC_ENUM,
                type(path).getInternalName(),
                null,
                org.objectweb.asm.Type.getInternalName(Enum.class),
                new String[]{type("org.machinemc.api.world.blockdata.BlockDataProperty").getInternalName()});
        CodeGenerator.visitGeneratedAnnotation(cw, BlockDataLibGenerator.class);

        // Fields
        for (final String value : values) {
            final FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC
                            | Opcodes.ACC_STATIC
                            | Opcodes.ACC_FINAL
                            | Opcodes.ACC_ENUM,
                    value.toUpperCase(),
                    type(path).getDescriptor(),
                    null,
                    null);
            fv.visitEnd();
            cw.visitEnd();
        }

        // Constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE,
                "<init>",
                "(Ljava/lang/String;I)V",
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                org.objectweb.asm.Type.getInternalName(Enum.class),
                "<init>",
                "(Ljava/lang/String;I)V",
                false);
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
        int i = 0;
        for (final String value : values) {
            mv.visitTypeInsn(Opcodes.NEW, type(path).getInternalName());
            mv.visitInsn(Opcodes.DUP);
            pushValue(mv, value.toUpperCase());
            pushValue(mv, i);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    type(path).getInternalName(),
                    "<init>",
                    "(Ljava/lang/String;I)V",
                    false);
            mv.visitFieldInsn(Opcodes.PUTSTATIC,
                    type(path).getInternalName(),
                    value.toUpperCase(),
                    type(path).getDescriptor());
            i++;
        }
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                type(path).getInternalName(),
                "$values",
                "()" + array(type(path)).getDescriptor(),
                false);
        mv.visitFieldInsn(Opcodes.PUTSTATIC,
                type(path).getInternalName(),
                "$VALUES",
                array(type(path)).getDescriptor());
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // getName method
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "getName",
                "()" + org.objectweb.asm.Type.getType(String.class).getDescriptor(),
                null,
                new String[0]);
        mv.visitAnnotation(org.objectweb.asm.Type.getType(Override.class).getDescriptor(), true).visitEnd();
        mv.visitEnd();
        mv.visitCode();
        pushValue(mv, name);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // $values method
        mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                "$values",
                "()" + array(type(path)).getDescriptor(),
                null,
                new String[0]);
        mv.visitCode();
        pushValue(mv, values.size());
        mv.visitTypeInsn(Opcodes.ANEWARRAY, type(path).getInternalName());
        i = 0;
        for (final String value : values) {
            mv.visitInsn(Opcodes.DUP);
            pushValue(mv, i);
            mv.visitFieldInsn(Opcodes.GETSTATIC,
                    type(path).getInternalName(),
                    value.toUpperCase(),
                    type(path).getDescriptor());
            mv.visitInsn(Opcodes.AASTORE);
            i++;
        }
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // valueOf method
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "valueOf",
                "(Ljava/lang/String;)" + type(path).getDescriptor(),
                null,
                new String[0]);
        mv.visitCode();
        mv.visitLdcInsn(type(path));
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                org.objectweb.asm.Type.getInternalName(Enum.class),
                "valueOf",
                "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;",
                false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, type(path).getInternalName());
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // values method
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "values",
                "()" + array(type(path)).getDescriptor(),
                null,
                new String[0]);
        mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                type(path).getInternalName(),
                "$VALUES",
                array(type(path)).getDescriptor());
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                array(type(path)).getDescriptor(),
                "clone",
                "()Ljava/lang/Object;",
                false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, array(type(path)).getInternalName());
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        final FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE
                        | Opcodes.ACC_STATIC
                        | Opcodes.ACC_FINAL
                        | Opcodes.ACC_SYNTHETIC,
                "$VALUES",
                array(type(path)).getDescriptor(),
                null,
                null);
        fv.visitEnd();
        cw.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * Generates interface class data for this property.
     * @return data for interface of this property
     */
    public byte[] generateInterface() {
        final String interfacePath = "org.machinemc.api.world.blockdata.interfaces.Has" + formattedName;
        final String descriptor = switch (getType()) {
            case BOOLEAN -> org.objectweb.asm.Type.BOOLEAN_TYPE.getDescriptor();
            case NUMBER -> org.objectweb.asm.Type.INT_TYPE.getDescriptor();
            case OTHER -> type(path).getDescriptor();
        };
        final ClassWriter cw = new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE,
                type(interfacePath).getInternalName(),
                null,
                org.objectweb.asm.Type.getInternalName(Object.class),
                new String[0]);
        CodeGenerator.visitGeneratedAnnotation(cw, BlockDataLibGenerator.class);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT,
                "get" + formattedName,
                "()" + descriptor,
                null,
                new String[0]);
        mv.visitEnd();
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT,
                "set" + formattedName,
                "(" + descriptor + ")V",
                null,
                new String[0]);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * @return dot path of the interface for this property
     */
    public String getInterfacePath() {
        return "org.machinemc.api.world.blockdata.interfaces.Has" + formattedName;
    }

    /**
     * @return property type of this property (not to get confused with asm type)
     */
    public Type getType() {
        if (type != null)
            return type;
        boolean isBoolean = true;
        for (final String value : values) {
            if (!(value.equalsIgnoreCase("true")
                    || value.equalsIgnoreCase("false"))) {
                isBoolean = false;
                break;
            }
        }
        if (isBoolean) {
            type = Type.BOOLEAN;
            return Type.BOOLEAN;
        }
        for (final String value : values) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                type = Type.OTHER;
                return Type.OTHER;
            }
        }
        type = Type.NUMBER;
        return Type.NUMBER;
    }

    public enum Type {
        BOOLEAN,
        NUMBER,
        OTHER
    }

}
