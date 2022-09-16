package me.pesekjak.machine.entities.player;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public enum Hand {

    LEFT,
    RIGHT;

    public int getId() {
        return ordinal();
    }

    public static @NotNull Hand fromID(@Range(from = 0, to = 1) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Hand type");
        return values()[id];
    }

}
