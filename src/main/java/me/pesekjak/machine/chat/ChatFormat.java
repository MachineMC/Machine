package me.pesekjak.machine.chat;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Range;

public enum ChatFormat {

    BLACK('0', 0x0) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.BLACK);
        }
    },
    DARK_BLUE('1', 0x1) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_BLUE);
        }
    },
    DARK_GREEN('2', 0x2) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_GREEN);
        }
    },
    DARK_CYAN('3', 0x3) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_AQUA);
        }
    },
    DARK_RED('4', 0x4) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_RED);
        }
    },
    DARK_PURPLE('5', 0x5) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_PURPLE);
        }
    },
    GOLD('6', 0x6) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GOLD);
        }
    },
    LIGHT_GRAY('7', 0x7) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GRAY);
        }
    },
    DARK_GRAY('8', 0x8) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.DARK_GRAY);
        }
    },
    BLUE('9', 0x9) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.BLUE);
        }
    },
    GREEN('a', 0xA) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.GREEN);
        }
    },
    CYAN('b', 0xB) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.AQUA);
        }
    },
    RED('c', 0xC) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.RED);
        }
    },
    LIGHT_PURPLE('d', 0xD) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.LIGHT_PURPLE);
        }
    },
    YELLOW('e', 0xE) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.YELLOW);
        }
    },
    WHITE('f', 0xF) {
        @Override
        public Style asStyle() {
            return Style.style(NamedTextColor.WHITE);
        }
    },
    OBFUSCATED('k', 0x10, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.OBFUSCATED);
        }
    },
    BOLD('l', 0x11, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.BOLD);
        }
    },
    STRIKETHROUGH('m', 0x12, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.STRIKETHROUGH);
        }
    },
    UNDERLINED('n', 0x13, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.UNDERLINED);
        }
    },
    ITALIC('o', 0x14, true) {
        @Override
        public Style asStyle() {
            return Style.style(TextDecoration.ITALIC);
        }
    },
    RESET('r', 0x15) {
        @Override
        public Style asStyle() {
            return Style.empty();
        }
    };

    public final char code;
    public final int intCode;
    public final boolean isFormat;
    public final boolean isColor;

    ChatFormat(char code, int intCode, boolean isFormat) {
        this.code = code;
        this.intCode = intCode;
        this.isFormat = isFormat;
        isColor = !isFormat && code != 'r';
    }

    ChatFormat(char code, int intCode) {
        this(code, intCode, false);
    }

    public abstract Style asStyle();

    @Override
    public String toString() {
        return new String(new char[]{ChatUtils.COLOR_CHAR, code});
    }

    public static ChatFormat byChar(char code) {
        for (ChatFormat value : values()) {
            if (value.code == code)
                return value;
        }
        return null;
    }

    public static ChatFormat byChar(String code) {
        if (code.length() != 1)
            return null;
        return byChar(code.charAt(0));
    }

    public static ChatFormat byCode(@Range(from = 0, to = 21) int code) {
        for (ChatFormat value : values()) {
            if (value.intCode == code)
                return value;
        }
        return null;
    }
}
