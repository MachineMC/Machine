package me.pesekjak.machine.codegen.blockdata;

import lombok.Getter;
import org.objectweb.asm.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static me.pesekjak.machine.codegen.blockdata.BlockDataLibGenerator.toCamelCase;

public class Property {

    @Getter
    private final String name;
    @Getter
    private final String path;
    @Getter
    private final Set<String> values = new LinkedHashSet<>();
    private Type type;

    public Property(String name) {
        this.name = name;
        path = "me.pesekjak.machine.world.blockdata." + name + "Property";
    }

    public void addValue(String value) {
        type = null;
        values.add(value);
    }

    public void merge(Property other) {
        for(String value : other.values)
            addValue(value);
    }

    public byte[] generate() {
        ClassWriter cw = new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER | Opcodes.ACC_ENUM,
                type(path).getInternalName(),
                null,
                org.objectweb.asm.Type.getInternalName(Enum.class),
                new String[0]);

        // Fields
        for(String value : values) {
            FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_ENUM,
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
        for(String value : values) {
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
        for(String value : values) {
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

        FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC,
                "$VALUES",
                array(type(path)).getDescriptor(),
                null,
                null);
        fv.visitEnd();
        cw.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    public byte[] generateInterface() {
        String interfacePath = "me.pesekjak.machine.world.blockdata.interfaces.Has" + name;
        String descriptor = switch (getType()) {
            case BOOLEAN -> org.objectweb.asm.Type.BOOLEAN_TYPE.getDescriptor();
            case NUMBER -> org.objectweb.asm.Type.INT_TYPE.getDescriptor();
            case OTHER -> type(path).getDescriptor();
        };
        ClassWriter cw = new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE,
                type(interfacePath).getInternalName(),
                null,
                org.objectweb.asm.Type.getInternalName(Object.class),
                new String[0]);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT,
                "get" + toCamelCase(name, true),
                "()" + descriptor,
                null,
                new String[0]);
        mv.visitEnd();
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT,
                "set" + toCamelCase(name, true),
                "(" + descriptor + ")V",
                null,
                new String[0]);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    public String getInterfacePath() {
        return "me.pesekjak.machine.world.blockdata.interfaces.Has" + name;
    }

    public Type getType() {
        if(type != null)
            return type;
        boolean isBoolean = true;
        for(String value : values) {
            if (!(value.equalsIgnoreCase("true") ||
                    value.equalsIgnoreCase("false"))) {
                isBoolean = false;
                break;
            }
        }
        if(isBoolean) {
            type = Type.BOOLEAN;
            return Type.BOOLEAN;
        }
        boolean isNumber = true;
        for(String value : values) {
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

    private org.objectweb.asm.Type type(String dotPath) {
        return org.objectweb.asm.Type.getType("L" + dotPath.replace(".", "/") + ";");
    }

    private org.objectweb.asm.Type array(org.objectweb.asm.Type type) {
        return org.objectweb.asm.Type.getType("[" + type.getDescriptor());
    }

    private void pushValue(final MethodVisitor mv, Object o) {
        int value;
        if(o instanceof Boolean)
            value = (Boolean) o ? 1 : 0;
        else if(o instanceof Character)
            value = (Character) o;
        else if(o instanceof Number)
            value = ((Number) o).intValue();
        else {
            mv.visitLdcInsn(o);
            return;
        }
        if(0 <= value && value <= 5)
            mv.visitInsn(Opcodes.ICONST_0 + value);
        else if(Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE)
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        else if(Short.MIN_VALUE <= value && value <= Short.MAX_VALUE)
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        else
            mv.visitLdcInsn(value);
    }

    public enum Type {
        BOOLEAN,
        NUMBER,
        OTHER
    }

}
