package me.pesekjak.machine.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public enum Difficulty {

    PEACEFUL(0, "peaceful"),
    EASY(1, "easy"),
    NORMAL(2, "normal"),
    HARD(3, "hard");

    public static final Difficulty DEFAULT_DIFFICULTY = EASY;

    @Getter
    private final int id;
    @Getter
    private final String name;

    public static Difficulty getByID(@Range(from = 0, to = 3) int id) {
        for (Difficulty value : values()) {
            if (value.id == id)
                return value;
        }
        return null;
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
