package me.pesekjak.machine.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public class ChatUtils {

    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder()
            .build();
    public static final char COLOR_CHAR = 167; // §
    public static final String DEFAULT_CHAT_FORMAT = "<%name%> %message%";

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static TextComponent stringToComponent(String string) {
        if (string == null)
            return Component.text("");
        return legacyComponentSerializer.deserialize(string);
    }

    public static String componentToString(Component component) {
        return legacyComponentSerializer.serialize(component);
    }

}
