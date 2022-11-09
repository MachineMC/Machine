package me.pesekjak.machine.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public final class ChatUtils {

    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().build();
    public static final char COLOR_CHAR = 167; // §
    private static final char CONSOLE_COLOR_CHAR = '\033';

    private static final Pattern AMP_COLOR_CODE_PATTERN = Pattern.compile("&([\\daAbBcCdDeEfFkKlLmMnNoOrR])");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(COLOR_CHAR + "([\\daAbBcCdDeEfFkKlLmMnNoOrR])");

    public static final String DEFAULT_CHAT_FORMAT = "<%name%> %message%";

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static String colored(String string) {
        return AMP_COLOR_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    public static TextComponent stringToComponent(String string) {
        if (string == null)
            return Component.text("");
        return legacyComponentSerializer.deserialize(string);
    }

    public static String componentToString(Component component) {
        return legacyComponentSerializer.serialize(component);
    }

    public static String consoleFormatted(String string) {
        return COLOR_CODE_PATTERN.matcher(colored(string)).replaceAll(matchResult -> {
            final ChatColor color = ChatColor.byChar(matchResult.group(1));
            if (color == null || color.consoleCode < 0)
                return matchResult.group();
            return CONSOLE_COLOR_CHAR + "[" + (color.isColor ? "0;" : "") + color.consoleCode + "m";
        });
    }

    public static String consoleFormatted(Component component) {
        return appendComponent(new StringBuilder(), component).toString();
    }

    private static StringBuilder appendComponent(StringBuilder builder, Component component) {
        if(component instanceof TextComponent textComponent) {
            builder.append(CONSOLE_COLOR_CHAR + "[").append(ChatColor.RESET.consoleCode).append("m");
            final Style style = textComponent.style();
            builder.append(asciiColor(style.color()));
            for(TextDecoration decoration : style.decorations().keySet()) {
                if(style.decorations().get(decoration) != TextDecoration.State.TRUE) continue;
                builder.append(CONSOLE_COLOR_CHAR + "[");
                switch (decoration) {
                    case BOLD -> builder.append(ChatColor.BOLD.consoleCode);
                    case ITALIC -> builder.append(ChatColor.ITALIC.consoleCode);
                    case OBFUSCATED -> builder.append(ChatColor.OBFUSCATED.consoleCode);
                    case UNDERLINED -> builder.append(ChatColor.UNDERLINED.consoleCode);
                    case STRIKETHROUGH -> builder.append(ChatColor.STRIKETHROUGH.consoleCode);
                }
                builder.append("m");
            }
            builder.append(textComponent.content());
        }
        for(Component child : component.children())
            appendComponent(builder, child);
        return builder;
    }

    private static String asciiColor(TextColor color) {
        return color != null ? "\u001B[38;2;" + color.red() + ";" + color.green() + ";" + color.blue() + "m" : "";
    }

}
