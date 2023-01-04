package me.pesekjak.machine.codegen;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CodeGenerator {

    public final static String prefix = "machine-";
    @Getter
    private final String libraryName;

    public final static String CONSTRUCTOR_NAME = "<init>";
    public final static String STATIC_NAME = "<clinit>";

    protected ZipOutputStream zip;
    protected final JsonParser parser = new JsonParser();
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
    private JsonObject source;

    @Getter(AccessLevel.PROTECTED)
    private boolean exists = false;

    protected CodeGenerator(@NotNull File outputDir, String libraryName) throws IOException {
        this.libraryName = libraryName;
        final File jar = new File(outputDir.getPath() + "/" + prefix + libraryName + ".jar");
        if(jar.exists()) {
            exists = true;
            return;
        }
        if(!jar.createNewFile())
            throw new IOException("Failed to create the jar file for " + libraryName + " Machine Library");
        zip = new ZipOutputStream(new FileOutputStream(jar));
        source = null;
    }

    protected CodeGenerator(File outputDir, String libraryName, String jsonFile) throws IOException {
        this(outputDir, libraryName);
        if(exists) return;
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(jsonFile);
        if(stream == null)
            throw new FileNotFoundException();
        source = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
    }

    public void generate() throws IOException {
        System.out.println("Successfully generated the " + libraryName + " library");
        zip.close();
    }

    public void addClass(String dotPath, byte[] data) throws IOException {
        ZipEntry e = new ZipEntry(type(dotPath).getInternalName() + ".class");
        zip.putNextEntry(e);
        zip.write(data, 0, data.length);
        zip.closeEntry();
    }

    public Type type(@NotNull String dotPath) {
        return Type.getType("L" + dotPath.replace(".", "/") + ";");
    }

    public Type array(@NotNull Type type) {
        return Type.getType("[" + type.getDescriptor());
    }

    public ClassWriter createWriter() {
        return new ClassWriter(
                Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
        );
    }

    public void pushValue(final MethodVisitor mv, Object o) {
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

}
