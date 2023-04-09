package org.machinemc.api.utils;

import com.google.common.base.Strings;
import org.machinemc.nbt.*;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        this("    ", 0);
    }

    public PrettyNBTStringVisitor(String indentation, int depth) {
        this.result = TextComponent.of("");
        this.indentation = indentation;
        this.depth = depth;
    }

    /**
     * Visits NBT and returns formatted component of this nbt.
     * @param nbt nbt
     * @return formatted nbt component
     */
    public TextComponent visitNBT(NBT nbt) {
        nbt.accept(this);
        return result;
    }

    @Override
    public void visit(NBTString nbtString) {
        String string = nbtString.toString();
        TextComponent component = text(string.substring(1, string.length() - 1), STRING_COLOR);
        char quote = string.charAt(0);
        TextComponent quoteComponent = TextComponent.of(quote + "").modify()
                .color(ChatColor.WHITE)
                .finish();
        quoteComponent.setText(quote + "");
        result.append(quoteComponent).append(component).append(quoteComponent);
    }

    @Override
    public void visit(NBTByte nbtByte) {
        result.append(text(nbtByte.value(), NUMBER_COLOR)).append(text('b', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTShort nbtShort) {
        result.append(text(nbtShort.value(), NUMBER_COLOR)).append(text('s', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTInt nbtInt) {
        result.append(text(nbtInt.value(), NUMBER_COLOR));
    }

    @Override
    public void visit(NBTLong nbtLong) {
        result.append(text(nbtLong.value(), NUMBER_COLOR)).append(text('L', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTFloat nbtFloat) {
        result.append(text(nbtFloat.value(), NUMBER_COLOR)).append(text('f', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTDouble nbtDouble) {
        result.append(text(nbtDouble.value(), NUMBER_COLOR)).append(text('d', DATA_TYPE_COLOR));
    }

    @Override
    public void visit(NBTByteArray nbtByteArray) {
        TextComponent dataType = text('B', DATA_TYPE_COLOR);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        byte[] bytes = nbtByteArray.value();

        for (int i = 0; i < bytes.length; i++) {
            TextComponent number = text(bytes[i], NUMBER_COLOR);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number).append(dataType);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(NBTIntArray nbtIntArray) {
        TextComponent dataType = text('I', DATA_TYPE_COLOR);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        int[] ints = nbtIntArray.value();

        for (int i = 0; i < ints.length; i++) {
            TextComponent number = text(ints[i], NUMBER_COLOR);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(NBTLongArray nbtLongArray) {
        TextComponent dataType = text('L', DATA_TYPE_COLOR);
        result.append(LEFT_BRACE).append(dataType).append(SEMI_COLON);
        long[] longs = nbtLongArray.value();

        for (int i = 0; i < longs.length; i++) {
            TextComponent number = text(longs[i], NUMBER_COLOR);
            if (i != 0)
                result.append(ELEMENT_SEPARATOR);

            result.append(" ").append(number).append(dataType);
        }

        result.append(RIGHT_BRACE);
    }

    @Override
    public void visit(NBTList nbtList) {
        if (nbtList.isEmpty()) {
            result.append(LEFT_BRACE).append(RIGHT_BRACE);
            return;
        }

        result.append(LEFT_BRACE);
        boolean hasIndentation = !indentation.isEmpty();
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
    public void visit(NBTCompound nbtCompound) {
        if (nbtCompound.isEmpty()) {
            result.append(LEFT_CURLY_BRACE).append(RIGHT_CURLY_BRACE);
            return;
        }

        result.append(LEFT_CURLY_BRACE);
        boolean hasIndentation = !indentation.isEmpty();
        if (hasIndentation)
            result.append("\n");
        List<String> keys = new ArrayList<>(nbtCompound.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
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
    public void visit(NBTEnd nbtEnd) {}

    /**
     * Returns text component with provided object as its content.
     * @param string component content
     * @return component
     */
    private static TextComponent text(Object string) {
        return TextComponent.of(String.valueOf(string));
    }

    /**
     * Returns colored text component with provided object as its content.
     * @param string component content
     * @param color color
     * @return component
     */
    private static TextComponent text(Object string, ChatColor color) {
        return TextComponent.of(string + "").modify().color(color).finish();
    }

    /**
     * Returns colored component out of value string.
     * @param string value
     * @return component
     */
    private static TextComponent handleEscape(String string) {
        if (SIMPLE_VALUE.matcher(string).matches()) {
            return TextComponent.of(string).modify().color(KEY_COLOR).finish();
        }
        string = NBTString.quoteAndEscape(string);
        char quote = string.charAt(0);
        TextComponent quoteComponent = TextComponent.of(quote + "").modify()
                .color(ChatColor.WHITE)
                .finish();
        return (TextComponent) TextComponent.of("").append(quoteComponent).append(string.substring(1, string.length() - 1)).append(quoteComponent);
    }

}
