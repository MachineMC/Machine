package me.pesekjak.machine.codegen.materials;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.pesekjak.machine.codegen.CodeGenerator;
import me.pesekjak.machine.codegen.blockdata.BlockDataLibGenerator;
import org.objectweb.asm.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class MaterialsLibGenerator extends CodeGenerator {

    private final Map<String, Integer> itemsMap = new TreeMap<>();
    @Getter
    private final String path = "me.pesekjak.machine.world.Material";

    public MaterialsLibGenerator() throws IOException {
        super("Materials", "registries.json");
    }

    @Override
    public void generate() throws IOException {
        System.out.println("Generating the " + super.getLibraryName() + " library");
        setSource(getSource().get("minecraft:item").getAsJsonObject()
                .get("entries").getAsJsonObject());
        for(Map.Entry<String, JsonElement> entry : getSource().entrySet())
            handleEntry(entry, true);

        // Getting the BlockData information
        String blockDataPath = "me.pesekjak.machine.world.BlockData";
        JsonParser parser = new JsonParser();
        final InputStream stream = getClass().getClassLoader().getResourceAsStream("blocks.json");
        if(stream == null)
            throw new FileNotFoundException();
        JsonObject blocksJson = parser.parse(new InputStreamReader(stream)).getAsJsonObject();

        for(Map.Entry<String, JsonElement> entry : blocksJson.entrySet())
            handleEntry(entry, false);

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
                type("me.pesekjak.machine.utils.NamespacedKey").getDescriptor(),
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
        fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "blockData",
                type(blockDataPath).getDescriptor(),
                null,
                null);
        fv.visitEnd();
        cw.visitEnd();

        // Constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE,
                CONSTRUCTOR_NAME,
                "(Ljava/lang/String;ILjava/lang/String;I" + type(blockDataPath).getDescriptor() + ")V",
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
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                type("me.pesekjak.machine.utils.NamespacedKey").getInternalName(),
                "minecraft",
                "(Ljava/lang/String;)Lme/pesekjak/machine/utils/NamespacedKey;",
                false);
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                type(path).getInternalName(),
                "name",
                type("me.pesekjak.machine.utils.NamespacedKey").getDescriptor());
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 4);
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                type(path).getInternalName(),
                "id",
                "I");

        // Setting the data if missing
        Label end = new Label();

        mv.visitVarInsn(Opcodes.ALOAD, 5);
        mv.visitJumpInsn(Opcodes.IFNULL, end);

        mv.visitVarInsn(Opcodes.ALOAD, 5);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                type(blockDataPath).getInternalName(),
                "setMaterial",
                "(" + type(path).getDescriptor() + ")" + type(blockDataPath).getDescriptor(),
                false);
        mv.visitInsn(Opcodes.POP);
        mv.visitJumpInsn(Opcodes.GOTO, end);

        mv.visitLabel(end);
        // Setting the blockdata
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 5);
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                type(path).getInternalName(),
                "blockData",
                type(blockDataPath).getDescriptor());
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        // Name getter
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "getName",
                "()Lme/pesekjak/machine/utils/NamespacedKey;",
                null,
                new String[0]);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                type(path).getInternalName(),
                "name",
                type("me.pesekjak.machine.utils.NamespacedKey").getDescriptor());
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

            if(blocksJson.get("minecraft:" + value) != null) {
                JsonObject blockJson = blocksJson.get("minecraft:" + value).getAsJsonObject();
                if(blockJson.get("properties") != null) {
                    String path = "me.pesekjak.machine.world." + BlockDataLibGenerator.toCamelCase(value, true) + "Data";
                    mv.visitTypeInsn(Opcodes.NEW, type(path).getInternalName());
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            type(path).getInternalName(),
                            "<init>",
                            "()V",
                            false);
                } else {
                    int id = blockJson.get("states").getAsJsonArray().get(0)
                            .getAsJsonObject().get("id").getAsInt();
                    mv.visitTypeInsn(Opcodes.NEW, type(blockDataPath).getInternalName());
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitLdcInsn(id);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            type(blockDataPath).getInternalName(),
                            "<init>",
                            "(I)V",
                            false);
                }
            } else {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }

            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    type(path).getInternalName(),
                    CONSTRUCTOR_NAME,
                    "(Ljava/lang/String;ILjava/lang/String;I" + type(blockDataPath).getDescriptor() + ")V",
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

        // BlockData cloner
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "createBlockData",
                "()" + type(blockDataPath).getDescriptor(),
                null,
                new String[0]);
        mv.visitCode();

        Label nullLabel = new Label();
        Label notNullLabel = new Label();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                type(path).getInternalName(),
                "blockData",
                type(blockDataPath).getDescriptor());
        mv.visitJumpInsn(Opcodes.IFNULL, nullLabel);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                type(path).getInternalName(),
                "blockData",
                type(blockDataPath).getDescriptor());
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                type(blockDataPath).getInternalName(),
                "clone",
                "()Lme/pesekjak/machine/world/BlockData;",
                false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitJumpInsn(Opcodes.GOTO, notNullLabel);

        mv.visitLabel(nullLabel);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitLabel(notNullLabel);

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
    }

    private void handleEntry(Map.Entry<String, JsonElement> entry, boolean hasId) {
        itemsMap.putIfAbsent(entry.getKey().replaceFirst("minecraft:", ""),
                hasId ? entry.getValue().getAsJsonObject().get("protocol_id").getAsInt() : -1);
    }

}
