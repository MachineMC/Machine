package org.machinemc.api.chat;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Default color values for Minecraft chat.
 */
public enum ChatColor {

    BLACK('0', 30) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.BLACK);
        }
    },
    DARK_BLUE('1', 34) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_BLUE);
        }
    },
    DARK_GREEN('2', 32) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_GREEN);
        }
    },
    DARK_CYAN('3', 36) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_AQUA);
        }
    },
    DARK_RED('4', 31) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_RED);
        }
    },
    DARK_PURPLE('5', 35) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_PURPLE);
        }
    },
    GOLD('6', 33) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GOLD);
        }
    },
    LIGHT_GRAY('7', 37) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GRAY);
        }
    },
    DARK_GRAY('8', 90) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_GRAY);
        }
    },
    BLUE('9', 94) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.BLUE);
        }
    },
    GREEN('a', 92) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GREEN);
        }
    },
    CYAN('b', 96) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.AQUA);
        }
    },
    RED('c', 91) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.RED);
        }
    },
    LIGHT_PURPLE('d', 95) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.LIGHT_PURPLE);
        }
    },
    YELLOW('e', 93) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.YELLOW);
        }
    },
    WHITE('f', 97) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.WHITE);
        }
    },
    OBFUSCATED('k', 5, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.OBFUSCATED);
        }
    },
    BOLD('l', 1, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.BOLD);
        }
    },
    STRIKETHROUGH('m', 9, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.STRIKETHROUGH);
        }
    },
    UNDERLINED('n', 4, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.UNDERLINED);
        }
    },
    ITALIC('o', 3, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.ITALIC);
        }
    },
    RESET('r', 0) {
        @Override
        public Style asStyle() {
            return Style.empty();
        }
    };

    public final char code;
    public final int consoleCode;
    public final boolean isFormat;
    public final boolean isColor;

    ChatColor(char code, int consoleCode, boolean isFormat) {
        this.code = code;
        this.consoleCode = consoleCode;
        this.isFormat = isFormat;
        this.isColor = !isFormat && code != 'r';
    }

    ChatColor(char code, int consoleCode) {
        this(code, consoleCode, false);
    }

    /**
     * @return numeric code of the chat color
     */
    public @Range(from = 0, to = 21) int getIntCode() {
        return ordinal();
    }

    /**
     * @return style of the chat color
     */
    public abstract Style asStyle();

    @Override
    public String toString() {
        return new String(new char[]{ChatUtils.COLOR_CHAR, code});
    }

    /**
     * Returns the chat color for the given character, null
     * if there is no chat color assigned to the character.
     * @param code character to get the chat color for
     * @return chat color mapped to given character
     */
    public static @Nullable ChatColor byChar(char code) {
        for (ChatColor value : values()) {
            if (value.code == code)
                return value;
        }
        return null;
    }

    /**
     * Returns the chat color for the given character, null
     * if there is no chat color assigned to the character.
     * @param code character to get the chat color for
     * @return chat color mapped to given character
     */
    public static @Nullable ChatColor byChar(String code) {
        if (code.length() != 1)
            return null;
        return byChar(code.charAt(0));
    }

    /**
     * Returns the chat color by its numeric code.
     * @param code numeric code of the chat color
     * @return chat color with given numeric code
     */
    public static ChatColor byCode(@Range(from = 0, to = 21) int code) {
        Preconditions.checkArgument(code < values().length, "Unsupported ChatColor");
        return values()[code];
    }

    /**
     * Returns the chat color of given style, null if there is no
     * chat color for given style.
     * @param style style of the chat color
     * @return chat color of the given style
     */
    public static @Nullable ChatColor byStyle(Style style) {
        for(ChatColor chatColor : values()) {
            if(chatColor.asStyle().equals(style)) return chatColor;
        }
        return null;
    }

}
