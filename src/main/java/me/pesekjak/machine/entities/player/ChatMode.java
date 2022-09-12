package me.pesekjak.machine.entities.player;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public enum ChatMode {

    ENABLED,
    COMMANDS_ONLY,
    HIDDEN;

    public int getId() {
        return ordinal();
    }

    public static @NotNull ChatMode fromID(@Range(from = 0, to = 2) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported ChatMode type");
        return values()[id];
    }

}
