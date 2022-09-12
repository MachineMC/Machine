package me.pesekjak.machine.world;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public enum Difficulty {

    PEACEFUL("peaceful"),
    EASY("easy"),
    NORMAL("normal"),
    HARD("hard");

    public static final Difficulty DEFAULT_DIFFICULTY = EASY;

    @Getter
    private final String name;

    public int getId() {
        return ordinal();
    }

    public static Difficulty getByID(@Range(from = 0, to = 3) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported difficulty type");
        return values()[id];
    }

    public static Difficulty getByName(String name) {
        if (name == null)
            return null;
        for (Difficulty value : values()) {
            if (value.name.equals(name))
                return value;
        }
        return null;
    }

}
