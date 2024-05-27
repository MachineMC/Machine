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
package org.machinemc.api.utils;

import com.google.common.base.Strings;
import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.*;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.TextFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * NBT String visitor for pretty formatted NBT.
 */
public class PrettyNBTStringVisitor implements NBTVisitor {

    private static final Pattern SIMPLE_VALUE = Pattern.compile("[\\dA-Za-z_\\-.+]+");
    private static final TextFormat KEY_FORMAT = new TextFormat(ChatColor.AQUA);
    private static final TextFormat NUMBER_FORMAT = new TextFormat(ChatColor.GOLD);
    private static final TextFormat STRING_FORMAT = new TextFormat(ChatColor.GREEN);
    private static final TextFormat DATA_TYPE_FORMAT = new TextFormat(ChatColor.RED);
    private static final TextComponent LEFT_CURLY_BRACE = TextComponent.of("{", new TextFormat(ChatColor.WHITE));
    private static final TextComponent RIGHT_CURLY_BRACE = TextComponent.of("}", new TextFormat(ChatColor.WHITE));
    private static final TextComponent LEFT_BRACE = TextComponent.of("[", new TextFormat(ChatColor.WHITE));
    private static final TextComponent RIGHT_BRACE = TextComponent.of("]", new TextFormat(ChatColor.WHITE));
    private static final TextComponent ELEMENT_SEPARATOR = TextComponent.of(",", new TextFormat(ChatColor.WHITE));
    private static final TextComponent COLON = TextComponent.of(":", new TextFormat(ChatColor.WHITE));
    private static final TextComponent SEMI_COLON = TextComponent.of(";", new TextFormat(ChatColor.WHITE));

    private final String indentation;
    private final int depth;
    private final TextComponent result;

    public PrettyNBTStringVisitor() {
        this(null, 0);
    }

    public PrettyNBTStringVisitor(final @Nullable String indentation, final int depth) {
        this.result = TextComponent.of("");
        this.indentation = indentation != null ? indentation : "    ";
        this.depth = depth;
    }

    /**
     * Visits NBT and returns formatted component of this nbt.
     * @param nbt nbt
     * @return formatted nbt component
     */
    public TextComponent visitNBT(final NBT<?> nbt) {
        Objects.requireNonNull(nbt);
        nbt.accept(this);
        return result;
    }

    @Override
    public void visit(final NBTString nbtString) {
        Objects.requireNonNull(nbtString);
        final String string = nbtString.toString();
        final TextComponent component = TextComponent.of(string.substring(1, string.length() - 1), STRING_FORMAT);
        final char quote = string.charAt(0);
        final TextComponent quoteComponent = TextComponent.of(String.valueOf(quote)).modify()
                .color(ChatColor.WHITE)
                .finish();
        quoteComponent.setText(String.valueOf(quote));
        result.append(quoteComponent).append(component).append(quoteComponent);
    }

    @Override
    public void visit(final NBTByte nbtByte) {
        Objects.requireNonNull(nbtByte);
        result.append(TextComponent.of(nbtByte.revert() + "", NUMBER_FORMAT))
                .append(TextComponent.of("b", DATA_TYPE_FORMAT));
    }

    @Override
    public void visit(final NBTShort nbtShort) {
        Objects.requireNonNull(nbtShort);
        result.append(TextComponent.of(nbtShort.revert() + "", NUMBER_FORMAT))
                .append(TextComponent.of("s", DATA_TYPE_FORMAT));
    }

    @Override
    public void visit(final NBTInt nbtInt) {
        Objects.requireNonNull(nbtInt);
        result.append(TextComponent.of(nbtInt.revert() + "", NUMBER_FORMAT));
    }

    @Override
    public void visit(final NBTLong nbtLong) {
        Objects.requireNonNull(nbtLong);
        result.append(TextComponent.of(nbtLong.revert() + "", NUMBER_FORMAT))
                .append(TextComponent.of("L", DATA_TYPE_FORMAT));
    }

    @Override
    public void visit(final NBTFloat nbtFloat) {
        Objects.requireNonNull(nbtFloat);
        result.append(TextComponent.of(nbtFloat.revert() + "", NUMBER_FORMAT))
                .append(TextComponent.of("f", DATA_TYPE_FORMAT));
    }

    @Override
    public void visit(final NBTDouble nbtDouble) {
        Objects.requireNonNull(nbtDouble);
        result.append(TextComponent.of(nbtDouble.revert() + "", NUMBER_FORMAT))
                .append(TextComponent.of("d", DATA_TYPE_FORMAT));
    }

    @Override
    public void visit(final NBTByteArray nbtByteArray) {
        Objects.requireNonNull(nbtByteArray);
        final TextComponent dataType = TextComponent.of("B", DATA_TYPE_FORMAT);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        final byte[] bytes = nbtByteArray.revert();

        for (int i = 0; i < bytes.length; i++) {
            final TextComponent number = TextComponent.of(bytes[i] + "", NUMBER_FORMAT);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number).append(dataType);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(final NBTIntArray nbtIntArray) {
        Objects.requireNonNull(nbtIntArray);
        final TextComponent dataType = TextComponent.of("I", DATA_TYPE_FORMAT);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        final int[] ints = nbtIntArray.revert();

        for (int i = 0; i < ints.length; i++) {
            final TextComponent number = TextComponent.of(ints[i] + "", NUMBER_FORMAT);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(final NBTLongArray nbtLongArray) {
        Objects.requireNonNull(nbtLongArray);
        final TextComponent dataType = TextComponent.of("L", DATA_TYPE_FORMAT);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        final long[] longs = nbtLongArray.revert();

        for (int i = 0; i < longs.length; i++) {
            final TextComponent number = TextComponent.of(longs[i] + "", NUMBER_FORMAT);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number).append(dataType);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(final NBTList nbtList) {
        Objects.requireNonNull(nbtList);
        if (nbtList.isEmpty()) {
            result.append(LEFT_BRACE).append(RIGHT_BRACE);
            return;
        }

        result.append(LEFT_BRACE);
        final boolean hasIndentation = !indentation.isEmpty();
        if (hasIndentation)
            result.append("\n");

        for (int i = 0; i < nbtList.size(); i++) {
            if (i != 0)
                result.append(ELEMENT_SEPARATOR).append(hasIndentation ? "\n" : " ");

            result.append(Strings.repeat(indentation, depth + 1))
                    .append(new PrettyNBTStringVisitor(indentation, depth + 1).visitNBT(nbtList.get(i)));
        }

        if (hasIndentation)
            result.append("\n").append(TextComponent.of(Strings.repeat(indentation, depth)));

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(final NBTCompound nbtCompound) {
        Objects.requireNonNull(nbtCompound);
        if (nbtCompound.isEmpty()) {
            result.append(LEFT_CURLY_BRACE).append(RIGHT_CURLY_BRACE);
            return;
        }

        result.append(LEFT_CURLY_BRACE);
        final boolean hasIndentation = !indentation.isEmpty();
        if (hasIndentation)
            result.append("\n");
        final List<String> keys = new ArrayList<>(nbtCompound.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR).append(hasIndentation ? "\n" : " ");

            result.append(Strings.repeat(indentation, depth + 1))
                    .append(handleEscape(key)).append(COLON).append(" ")
                    .append(new PrettyNBTStringVisitor(indentation, depth + 1).visitNBT(nbtCompound.getNBT(key)));
        }

        if (hasIndentation)
            result.append("\n").append(Strings.repeat(indentation, depth));

        result.append(RIGHT_CURLY_BRACE);
    }

    @Override
    public void visit(final NBTEnd nbtEnd) { }

    /**
     * Returns colored component out of value string.
     * @param string value
     * @return component
     */
    private static TextComponent handleEscape(final String string) {
        if (SIMPLE_VALUE.matcher(string).matches()) {
            return TextComponent.of(string, KEY_FORMAT);
        }
        final String escaped = NBTString.quoteAndEscape(string);
        final char quote = escaped.charAt(0);
        final TextComponent quoteComponent = TextComponent.of(String.valueOf(quote)).modify()
                .color(ChatColor.WHITE)
                .finish();
        return TextComponent.empty()
                .append(quoteComponent)
                .append(escaped.substring(1, escaped.length() - 1))
                .append(quoteComponent);
    }

}
