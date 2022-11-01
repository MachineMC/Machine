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
    private static final char CONSOLE_COLOR_CHAR = '\033';
    private static final Pattern AMP_COLOR_CODE_PATTERN = Pattern.compile("&([\\daAbBcCdDeEfFkKlLmMnNoOrR])");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(COLOR_CHAR + "([\\daAbBcCdDeEfFkKlLmMnNoOrR])");

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static String colored(String string) {
        return AMP_COLOR_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    public static String consoleFormatted(String string) {
        return COLOR_CODE_PATTERN.matcher(string + ChatColor.RESET).replaceAll(matchResult -> {
            ChatColor color = ChatColor.byChar(matchResult.group(1));
            if (color == null || color.consoleCode < 0)
                return matchResult.group();
            return CONSOLE_COLOR_CHAR + "[" + (color.isColor ? "0;" : "") + color.consoleCode + "m";
        });
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
