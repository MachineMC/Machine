package me.pesekjak.machine.codegen.materials;

import com.google.gson.JsonElement;
import lombok.Getter;
import me.pesekjak.machine.codegen.CodeGenerator;
import org.objectweb.asm.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MaterialsLibGenerator extends CodeGenerator {

    private final Map<String, Integer> itemsMap = new HashMap<>();
    @Getter
    private final String path = "me.pesekjak.machine.world.Material";

    public MaterialsLibGenerator() throws IOException {
        super("Materials", "registries.json");
    }

    @Override
    public void generate() throws IOException {
        setSource(getSource().get("minecraft:item").getAsJsonObject()
                .get("entries").getAsJsonObject());
        for(Map.Entry<String, JsonElement> entry : getSource().entrySet())
            handleEntry(entry);
        System.out.println("Loaded " + itemsMap.keySet().size() + " materials");
        System.out.println("Generating the class...");

        ClassWriter cw = createWriter();
        cw.visit(Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER | Opcodes.ACC_ENUM,
                type(path).getInternalName(),
                null,
                Type.getInternalName(Enum.class),
                new String[0]);

        // Fields
        for(String value : itemsMap.keySet()) {
            FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_ENUM,
                    value.toUpperCase(),
                    type(path).getDescriptor(),
                    null,
                    null);
            fv.visitEnd();
            cw.visitEnd();
        }
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "name",
                Type.getType(String.class).getDescriptor(),
                null,
                null);
        fv.visitEnd();
        cw.visitEnd();
        fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "id",
                Type.INT_TYPE.getDescriptor(),
                null,
                null);
        fv.visitEnd();
        cw.visitEnd();

        // Constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE,
                CONSTRUCTOR_NAME,
                "(Ljava/lang/String;ILjava/lang/String;I)V",
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getInternalName(Enum.class),
                CONSTRUCTOR_NAME,
                "(Ljava/lang/String;I)V",
                false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                type(path).getInternalName(),
                "name",
                "Ljava/lang/String;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 4);
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                type(path).getInternalName(),
                "id",
                "I");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // Name getter
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "getName",
                "()Ljava/lang/String;",
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                type(path).getInternalName(),
                "name",
                Type.getType(String.class).getDescriptor());
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // Id getter
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "getId",
                "()I",
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                type(path).getInternalName(),
                "id",
                Type.INT_TYPE.getDescriptor());
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // Static block
        mv = cw.visitMethod(Opcodes.ACC_STATIC,
                STATIC_NAME,
                "()V",
                null,
                new String[0]);
        int i = 0;
        for(String value : itemsMap.keySet()) {
            mv.visitTypeInsn(Opcodes.NEW, type(path).getInternalName());
            mv.visitInsn(Opcodes.DUP);
            pushValue(mv, value.toUpperCase());
            pushValue(mv, i);
            pushValue(mv, value);
            pushValue(mv, itemsMap.get(value));
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    type(path).getInternalName(),
                    CONSTRUCTOR_NAME,
                    "(Ljava/lang/String;ILjava/lang/String;I)V",
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
        pushValue(mv, itemsMap.size());
        mv.visitTypeInsn(Opcodes.ANEWARRAY, type(path).getInternalName());
        i = 0;
        for(String value : itemsMap.keySet()) {
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
                Type.getInternalName(Enum.class),
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

        fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC,
                "$VALUES",
                array(type(path)).getDescriptor(),
                null,
                null);
        fv.visitEnd();
        cw.visitEnd();

        cw.visitEnd();
        addClass(path, cw.toByteArray());
        super.generate();
        System.out.println("Successfully generated the library");
    }

    private void handleEntry(Map.Entry<String, JsonElement> entry) {
        itemsMap.put(entry.getKey().replaceFirst("minecraft:", ""),
                entry.getValue().getAsJsonObject().get("protocol_id").getAsInt());
    }

}
