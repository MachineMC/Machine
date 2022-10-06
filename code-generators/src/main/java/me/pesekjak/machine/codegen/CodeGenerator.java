package me.pesekjak.machine.codegen;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CodeGenerator {

    public final static String prefix = "Machine";
    @Getter
    private final String libraryName;

    public final static String CONSTRUCTOR_NAME = "<init>";
    public final static String STATIC_NAME = "<clinit>";

    protected final ZipOutputStream zip;
    protected final JsonParser parser = new JsonParser();
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
    private JsonObject source;

    protected CodeGenerator(String libraryName) throws IOException {
        boolean s = new File(Generators.OUTPUT_PATH + prefix + libraryName + ".jar").createNewFile();
        this.libraryName = libraryName;
        zip = new ZipOutputStream(
                new FileOutputStream(Generators.OUTPUT_PATH + prefix + libraryName + ".jar"));
        source = null;
    }

    protected CodeGenerator(String libraryName, String jsonFile) throws IOException {
        this(libraryName);
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

    public Type type(String dotPath) {
        return Type.getType("L" + dotPath.replace(".", "/") + ";");
    }

    public Type array(Type type) {
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
    }

}
