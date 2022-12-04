package me.pesekjak.machine.entities.player;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Representing player's gamemode.
 */
public enum Gamemode {

    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    /**
     * @return numeric id of the gamemode used by Minecraft protocol.
     */
    public @Range(from = 0, to = 3) int getId() {
        return ordinal();
    }

    /**
     * Returns gamemode from its numeric id.
     * @param id id of the gamemode
     * @return gamemode for given id
     */
    public static @NotNull Gamemode fromID(@Range(from = 0, to = 3) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Gamemode type");
        return values()[id];
    }

    /**
     * Returns gamemode from its numeric id, supporting -1
     * as null value.
     * @param id id of the gamemode
     * @return gamemode for given id
     */
    public static @Nullable Gamemode nullableFromID(@Range(from = -1, to = 3) int id) {
        if (id == -1)
            return null;
        Preconditions.checkArgument(id < values().length, "Unsupported Gamemode type");
        return values()[id];
    }

    /**
     * Returns gamemode of given name.
     * @param name name of the gamemode
     * @return gamemode with given name
     */
    public static @Nullable Gamemode getByName(@NotNull String name) {
        for (Gamemode value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
