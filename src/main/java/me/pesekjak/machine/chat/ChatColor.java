package me.pesekjak.machine.chat;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Range;

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
    OBFUSCATED('k', -1, true) {
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

    public int getIntCode() {
        return ordinal();
    }

    public abstract Style asStyle();

    @Override
    public String toString() {
        return new String(new char[]{ChatUtils.COLOR_CHAR, code});
    }

    public static ChatColor byChar(char code) {
        for (ChatColor value : values()) {
            if (value.code == code)
                return value;
        }
        return null;
    }

    public static ChatColor byChar(String code) {
        if (code.length() != 1)
            return null;
        return byChar(code.charAt(0));
    }

    public static ChatColor byCode(@Range(from = 0, to = 21) int code) {
        Preconditions.checkArgument(code < values().length, "Unsupported ChatColor");
        return values()[code];

    }
}
