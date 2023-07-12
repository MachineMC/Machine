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
    private static final ChatColor KEY_COLOR = ChatColor.AQUA;
    private static final ChatColor NUMBER_COLOR = ChatColor.GOLD;
    private static final ChatColor STRING_COLOR = ChatColor.GREEN;
    private static final ChatColor DATA_TYPE_COLOR = ChatColor.RED;
    private static final TextComponent LEFT_CURLY_BRACE = TextComponent.of("{").modify()
            .color(ChatColor.WHITE)
            .finish();
    private static final TextComponent RIGHT_CURLY_BRACE = TextComponent.of("}").modify()
            .color(ChatColor.WHITE)
            .finish();
    private static final TextComponent LEFT_BRACE = TextComponent.of("[").modify()
            .color(ChatColor.WHITE)
            .finish();
    private static final TextComponent RIGHT_BRACE = TextComponent.of("]").modify()
            .color(ChatColor.WHITE)
            .finish();
    private static final TextComponent ELEMENT_SEPARATOR = TextComponent.of(",").modify()
            .color(ChatColor.WHITE)
            .finish();
    private static final TextComponent COLON = TextComponent.of(":").modify()
            .color(ChatColor.WHITE)
            .finish();
    private static final TextComponent SEMI_COLON = TextComponent.of(";").modify()
            .color(ChatColor.WHITE)
            .finish();

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
    public TextComponent visitNBT(final NBT nbt) {
        Objects.requireNonNull(nbt);
        nbt.accept(this);
        return result;
    }

    @Override
    public void visit(final NBTString nbtString) {
        Objects.requireNonNull(nbtString);
        final String string = nbtString.toString();
        final TextComponent component = text(string.substring(1, string.length() - 1), STRING_COLOR);
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
        result.append(text(nbtByte.value(), NUMBER_COLOR)).append(text('b', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(final NBTShort nbtShort) {
        Objects.requireNonNull(nbtShort);
        result.append(text(nbtShort.value(), NUMBER_COLOR)).append(text('s', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(final NBTInt nbtInt) {
        Objects.requireNonNull(nbtInt);
        result.append(text(nbtInt.value(), NUMBER_COLOR));
    }

    @Override
    public void visit(final NBTLong nbtLong) {
        Objects.requireNonNull(nbtLong);
        result.append(text(nbtLong.value(), NUMBER_COLOR)).append(text('L', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(final NBTFloat nbtFloat) {
        Objects.requireNonNull(nbtFloat);
        result.append(text(nbtFloat.value(), NUMBER_COLOR)).append(text('f', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(final NBTDouble nbtDouble) {
        Objects.requireNonNull(nbtDouble);
        result.append(text(nbtDouble.value(), NUMBER_COLOR)).append(text('d', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(final NBTByteArray nbtByteArray) {
        Objects.requireNonNull(nbtByteArray);
        final TextComponent dataType = text('B', DATA_TYPE_COLOR);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        final byte[] bytes = nbtByteArray.value();

        for (int i = 0; i < bytes.length; i++) {
            final TextComponent number = text(bytes[i], NUMBER_COLOR);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number).append(dataType);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(final NBTIntArray nbtIntArray) {
        Objects.requireNonNull(nbtIntArray);
        final TextComponent dataType = text('I', DATA_TYPE_COLOR);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        final int[] ints = nbtIntArray.value();

        for (int i = 0; i < ints.length; i++) {
            final TextComponent number = text(ints[i], NUMBER_COLOR);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(final NBTLongArray nbtLongArray) {
        Objects.requireNonNull(nbtLongArray);
        final TextComponent dataType = text('L', DATA_TYPE_COLOR);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        final long[] longs = nbtLongArray.value();

        for (int i = 0; i < longs.length; i++) {
            final TextComponent number = text(longs[i], NUMBER_COLOR);
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
            result.append("\n").append(text(Strings.repeat(indentation, depth)));

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
                    .append(new PrettyNBTStringVisitor(indentation, depth + 1).visitNBT(nbtCompound.get(key)));
        }

        if (hasIndentation)
            result.append("\n").append(Strings.repeat(indentation, depth));

        result.append(RIGHT_CURLY_BRACE);
    }

    @Override
    public void visit(final NBTEnd nbtEnd) { }

    /**
     * Returns text component with provided object as its content.
     * @param string component content
     * @return component
     */
    private static TextComponent text(final Object string) {
        return TextComponent.of(String.valueOf(string));
    }

    /**
     * Returns colored text component with provided object as its content.
     * @param string component content
     * @param color color
     * @return component
     */
    private static TextComponent text(final Object string, final ChatColor color) {
        return TextComponent.of(String.valueOf(string)).modify().color(color).finish();
    }

    /**
     * Returns colored component out of value string.
     * @param string value
     * @return component
     */
    private static TextComponent handleEscape(final String string) {
        if (SIMPLE_VALUE.matcher(string).matches()) {
            return TextComponent.of(string).modify().color(KEY_COLOR).finish();
        }
        final String escaped = NBTString.quoteAndEscape(string);
        final char quote = escaped.charAt(0);
        final TextComponent quoteComponent = TextComponent.of(String.valueOf(quote)).modify()
                .color(ChatColor.WHITE)
                .finish();
        return (TextComponent) TextComponent.of("")
                .append(quoteComponent)
                .append(escaped.substring(1, escaped.length() - 1))
                .append(quoteComponent);
    }

}
