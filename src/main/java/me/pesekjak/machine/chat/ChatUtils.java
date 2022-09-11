package me.pesekjak.machine.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public class ChatUtils {

    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .build();
    public static final char COLOR_CHAR = 167; // §
    public static final Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)(?=" + COLOR_CHAR + "([\\da-fk-or]))");

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static TextComponent parse(String string) {
        if (string == null)
            return Component.text("");
        return legacyComponentSerializer.deserialize(string);
    }

}
