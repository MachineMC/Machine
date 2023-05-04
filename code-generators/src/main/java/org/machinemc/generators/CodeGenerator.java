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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

import javax.annotation.processing.Generated;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CodeGenerator {

    public static final String PREFIX = "machine-";
    @Getter
    private final String libraryName;

    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String STATIC_NAME = "<clinit>";

    protected ZipOutputStream zip;
    protected final JsonParser parser = new JsonParser();
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
    private JsonObject source;

    @Getter(AccessLevel.PROTECTED)
    private boolean exists = false;

    protected CodeGenerator(final @NotNull File outputDir, final String libraryName) throws IOException {
        this.libraryName = libraryName;
        final File jar = new File(outputDir.getPath() + "/" + PREFIX + libraryName + ".jar");
        if (jar.exists()) {
            exists = true;
            return;
        }
        if (!jar.createNewFile())
            throw new IOException("Failed to create the jar file for " + libraryName + " Machine Library");
        zip = new ZipOutputStream(new FileOutputStream(jar));
        source = null;
    }

    protected CodeGenerator(final File outputDir,
                            final String libraryName,
                            final String jsonFile) throws IOException {
        this(outputDir, libraryName);
        if (exists) return;
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(jsonFile);
        if (stream == null)
            throw new FileNotFoundException();
        source = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
    }

    /**
     * Finishes the generation of the library.
     */
    public void generate() throws IOException {
        System.out.println("Successfully generated the " + libraryName + " library");
        zip.close();
    }

    /**
     * Adds class to the generated jar.
     * @param dotPath dot path of the class.
     * @param data data of the class
     */
    public void addClass(final String dotPath, final byte[] data) throws IOException {
        final ZipEntry e = new ZipEntry(type(dotPath).getInternalName() + ".class");
        zip.putNextEntry(e);
        zip.write(data, 0, data.length);
        zip.closeEntry();
    }

    /**
     * Creates type out of a class dot path.
     * @param dotPath dot path
     * @return type
     */
    public static Type type(final @NotNull String dotPath) {
        return Type.getType("L" + dotPath.replace(".", "/") + ";");
    }

    /**
     * Returns array type of given type.
     * @param type type
     * @return array type
     */
    public static Type array(final @NotNull Type type) {
        return Type.getType("[" + type.getDescriptor());
    }

    /**
     * Creates new class writer with default options.
     * @return new class writer
     */
    public static ClassWriter createWriter() {
        return new ClassWriter(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    /**
     * Pushes the value to the stack.
     * @param mv visitor
     * @param o value
     */
    public static void pushValue(final MethodVisitor mv, final Object o) {
        int value;
        if (o instanceof Boolean)
            value = (Boolean) o ? 1 : 0;
        else if (o instanceof Character)
            value = (Character) o;
        else if (o instanceof Number)
            value = ((Number) o).intValue();
        else {
            mv.visitLdcInsn(o);
            return;
        }
        if (0 <= value && value <= 5)
            mv.visitInsn(Opcodes.ICONST_0 + value);
        else if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE)
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE)
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        else
            mv.visitLdcInsn(value);
    }

    /**
     * Visits the generated annotation for the class visitor.
     * @param cv class visitor
     * @param codeGenerator code generator that generated the annotated class
     */
    public static void visitGeneratedAnnotation(final ClassVisitor cv,
                                                final Class<? extends CodeGenerator> codeGenerator) {
        final AnnotationVisitor av = cv.visitAnnotation(Type.getType(Generated.class).getDescriptor(), true);

        final AnnotationVisitor valueVisitor = av.visitArray("value");
        valueVisitor.visit("0", codeGenerator.getName());
        valueVisitor.visitEnd();

        final TimeZone tz = TimeZone.getTimeZone("UTC");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        av.visit("date", df.format(Date.from(Instant.now())));

        av.visitEnd();
    }

    /**
     * Converts string to camel case.
     * @param text text to convert
     * @param capitalizeFirst whether the first letter should be capitalize
     * @return camel case text
     */
    public static String toCamelCase(final String text, final boolean capitalizeFirst) {
        final String[] words = text.split("[\\W_]+");
        final StringBuilder builder = new StringBuilder();
        boolean capitalize = capitalizeFirst;
        for (final String word : words) {
            final String formattedWord;
            if (capitalize)
                formattedWord = word.isEmpty()
                        ? word
                        : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            else {
                formattedWord = word.toLowerCase();
                capitalize = true;
            }
            builder.append(formattedWord);
        }
        return builder.toString();
    }

}
