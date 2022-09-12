package me.pesekjak.machine.entities;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public enum Gamemode {

    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    public int getId() {
        return ordinal();
    }

    public static @NotNull Gamemode fromID(@Range(from = 0, to = 3) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Gamemode type");
        return values()[id];
    }

}
