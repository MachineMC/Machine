package me.pesekjak.machine.world;

import org.jetbrains.annotations.Nullable;

public enum WorldType {

    NORMAL,
    FLAT;

    @Nullable
    public static WorldType getByName(String name) {
        if (name == null)
            return null;
        for (WorldType value : values()) {
            if (value.name().equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
