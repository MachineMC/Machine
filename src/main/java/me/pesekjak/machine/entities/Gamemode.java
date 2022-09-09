package me.pesekjak.machine.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public enum Gamemode {

    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    @Getter
    private final int ID;

    public static @NotNull Gamemode fromID(@Range(from = 0, to = 3) int ID) {
        for (Gamemode gamemode : Gamemode.values()) {
            if (gamemode.getID() == ID) return gamemode;
        }
        throw new RuntimeException("Unsupported Gamemode type");
    }

}
