package me.pesekjak.machine.chat;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Range;

public enum ChatColor {

    BLACK('0') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.BLACK);
        }
    },
    DARK_BLUE('1') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_BLUE);
        }
    },
    DARK_GREEN('2') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_GREEN);
        }
    },
    DARK_CYAN('3') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_AQUA);
        }
    },
    DARK_RED('4') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_RED);
        }
    },
    DARK_PURPLE('5') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_PURPLE);
        }
    },
    GOLD('6') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GOLD);
        }
    },
    LIGHT_GRAY('7') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GRAY);
        }
    },
    DARK_GRAY('8') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_GRAY);
        }
    },
    BLUE('9') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.BLUE);
        }
    },
    GREEN('a') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GREEN);
        }
    },
    CYAN('b') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.AQUA);
        }
    },
    RED('c') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.RED);
        }
    },
    LIGHT_PURPLE('d') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.LIGHT_PURPLE);
        }
    },
    YELLOW('e') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.YELLOW);
        }
    },
    WHITE('f') {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.WHITE);
        }
    },
    OBFUSCATED('k', true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.OBFUSCATED);
        }
    },
    BOLD('l', true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.BOLD);
        }
    },
    STRIKETHROUGH('m', true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.STRIKETHROUGH);
        }
    },
    UNDERLINED('n', true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.UNDERLINED);
        }
    },
    ITALIC('o', true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.ITALIC);
        }
    },
    RESET('r') {
        @Override
        public Style asStyle() {
            return Style.empty();
        }
    };

    public final char code;
    public final boolean isFormat;
    public final boolean isColor;

    ChatColor(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        isColor = !isFormat && code != 'r';
    }

    ChatColor(char code) {
        this(code, false);
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
