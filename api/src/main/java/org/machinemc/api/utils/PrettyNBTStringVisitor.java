package org.machinemc.api.utils;

import com.google.common.base.Strings;
import org.machinemc.api.chat.ChatColor;
import net.kyori.adventure.text.Component;
import org.machinemc.nbt.*;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * NBT String visitor for pretty formatted NBT.
 */
public class PrettyNBTStringVisitor implements NBTVisitor {

    private static final Pattern SIMPLE_VALUE = Pattern.compile("[\\dA-Za-z_\\-.+]+");
    private static final ChatColor KEY_COLOR = ChatColor.CYAN;
    private static final ChatColor NUMBER_COLOR = ChatColor.GOLD;
    private static final ChatColor STRING_COLOR = ChatColor.GREEN;
    private static final ChatColor DATA_TYPE_COLOR = ChatColor.RED;

    private final String indentation;
    private final int depth;
    private Component result;

    public PrettyNBTStringVisitor() {
        this("    ", 0);
    }

    public PrettyNBTStringVisitor(String indentation, int depth) {
        this.result = Component.empty();
        this.indentation = indentation;
        this.depth = depth;
    }

    /**
     * Visits NBT and returns formatted component of this nbt.
     * @param nbt nbt
     * @return formatted nbt component
     */
    public Component visitNBT(NBT nbt) {
        nbt.accept(this);
        return result;
    }

    @Override
    public void visit(NBTString nbtString) {
        String string = nbtString.toString();
        char quote = string.charAt(0);
        Component component = text(string.substring(1, string.length() - 1), STRING_COLOR);
        result = text(quote).append(component).append(text(quote));
    }

    @Override
    public void visit(NBTByte nbtByte) {
        result = text(nbtByte.value(), NUMBER_COLOR).append(text('b', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTShort nbtShort) {
        result = text(nbtShort.value(), NUMBER_COLOR).append(text('s', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTInt nbtInt) {
        result = text(nbtInt.value(), NUMBER_COLOR);
    }

    @Override
    public void visit(NBTLong nbtLong) {
        result = text(nbtLong.value(), NUMBER_COLOR).append(text('L', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTFloat nbtFloat) {
        result = text(nbtFloat.value(), NUMBER_COLOR).append(text('f', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTDouble nbtDouble) {
        result = text(nbtDouble.value(), NUMBER_COLOR).append(text('d', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTByteArray nbtByteArray) {
        Component dataType = text('B', DATA_TYPE_COLOR);
        result = text('[').append(dataType).append(text(';'));
        byte[] bytes = nbtByteArray.value();

        for (int i = 0; i < bytes.length; i++) {
            Component number = text(bytes[i], NUMBER_COLOR);
            if (i != 0)
                append(text(','));

            append(text(' ').append(number).append(dataType));
        }

        append(text(']'));
    }

    @Override
    public void visit(NBTIntArray nbtIntArray) {
        Component dataType = text('I', DATA_TYPE_COLOR);
        result = text('[').append(dataType).append(text(';'));
        int[] ints = nbtIntArray.value();

        for (int i = 0; i < ints.length; i++) {
            Component number = text(ints[i], NUMBER_COLOR);
            if (i != 0)
                append(text(','));

            append(text(' ').append(number));
        }

        append(text(']'));
    }

    @Override
    public void visit(NBTLongArray nbtLongArray) {
        Component dataType = text('L', DATA_TYPE_COLOR);
        result = text('[').append(dataType).append(text(';'));
        long[] longs = nbtLongArray.value();

        for (int i = 0; i < longs.length; i++) {
            Component number = text(longs[i], NUMBER_COLOR);
            if (i != 0)
                append(text(','));

            append(text(' ').append(number).append(dataType));
        }

        append(text(']'));
    }

    @Override
    public void visit(NBTList nbtList) {
        if (nbtList.isEmpty()) {
            append(text("[]"));
            return;
        }

        result = text('[');
        boolean hasIndentation = !indentation.isEmpty();
        if (hasIndentation)
            append('\n');

        for (int i = 0; i < nbtList.size(); i++) {
            if (i != 0)
                append(text(',').append(text(hasIndentation ? '\n' : ' ')));

            append(text(Strings.repeat(indentation, depth + 1))
                    .append(new PrettyNBTStringVisitor(indentation, depth + 1).visitNBT(nbtList.get(i))));
        }

        if (hasIndentation)
            append(text('\n').append(text(Strings.repeat(indentation, depth))));

        append(']');
    }

    @Override
    public void visit(NBTCompound nbtCompound) {
        if (nbtCompound.isEmpty()) {
            append(text("{}"));
            return;
        }

        append('{');
        boolean hasIndentation = !indentation.isEmpty();
        if (hasIndentation)
            append('\n');
        List<String> keys = new ArrayList<>(nbtCompound.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i != 0)
                append(text(',').append(text(hasIndentation ? '\n' : ' ')));

            append(text(Strings.repeat(indentation, depth + 1))
                    .append(handleEscape(key)).append(text(':')).append(text(' '))
                    .append(new PrettyNBTStringVisitor(indentation, depth + 1).visitNBT(nbtCompound.get(key))));
        }

        if (hasIndentation)
            append(text('\n').append(text(Strings.repeat(indentation, depth))));

        append('}');
    }

    @Override
    public void visit(NBTEnd nbtEnd) {}

    @Override
    public String toString() {
        return "uilder.toString();";
    }

    /**
     * Returns text component with provided object as its content.
     * @param string component content
     * @return component
     */
    private Component text(Object string) {
        return Component.text(String.valueOf(string));
    }

    /**
     * Returns colored text component with provided object as its content.
     * @param string component content
     * @param color color
     * @return component
     */
    private Component text(Object string, ChatColor color) {
        return Component.text(string + "", color.asStyle());
    }

    /**
     * Appends string to the result component of this visitor.
     * @param string string to append
     * @return new result component
     */
    private Component append(Object string) {
        return append(text(string));
    }

    /**
     * Appends colored string to the result component of this visitor.
     * @param string string to append
     * @param color color
     * @return new result component
     */
    private Component append(Object string, ChatColor color) {
        return append(text(string, color));
    }

    /**
     * Appends component to the result component of this visitor.
     * @param component component to append
     * @return new result component
     */
    private Component append(Component component) {
        result = result.append(component);
        return result;
    }

    /**
     * Returns colored component out of value string.
     * @param string value
     * @return component
     */
    private static Component handleEscape(String string) {
        if (SIMPLE_VALUE.matcher(string).matches()) {
            return Component.text(string, KEY_COLOR.asStyle());
        }
        string = NBTString.quoteAndEscape(string);
        char quote = string.charAt(0);
        return Component.text(quote).append(Component.text(string.substring(1, string.length() - 1))).append(Component.text(quote));
    }

}