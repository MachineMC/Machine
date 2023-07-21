package org.machinemc.api.scoreboard;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Optional;

/**
 * Display position of an objective.
 */
public enum ObjectivePosition {

    LIST,
    SIDEBAR,
    BELOW_NAME,
    SIDEBAR_TEAM_BLACK,
    SIDEBAR_TEAM_DARK_BLUE,
    SIDEBAR_TEAM_DARK_GREEN,
    SIDEBAR_TEAM_DARK_AQUA,
    SIDEBAR_TEAM_DARK_RED,
    SIDEBAR_TEAM_DARK_PURPLE,
    SIDEBAR_TEAM_GOLD,
    SIDEBAR_TEAM_GRAY,
    SIDEBAR_TEAM_DARK_GRAY,
    SIDEBAR_TEAM_BLUE,
    SIDEBAR_TEAM_GREEN,
    SIDEBAR_TEAM_AQUA,
    SIDEBAR_TEAM_RED,
    SIDEBAR_TEAM_LIGHT_PURPLE,
    SIDEBAR_TEAM_YELLOW,
    SIDEBAR_TEAM_WHITE;

    /**
     * @return numeric id of the objective position used by Minecraft protocol.
     */
    public @Range(from = 0, to = 18) int getID() {
        return ordinal();
    }

    /**
     * Returns objective position from its numeric id.
     * @param id id of the objective position
     * @return objective position for given id
     */
    public static ObjectivePosition fromID(final @Range(from = 0, to = 18) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported objective display position");
        return values()[id];
    }

    /**
     * Returns objective position of given name.
     * @param name name of the objective position
     * @return objective position with given name
     */
    public static Optional<ObjectivePosition> getByName(final String name) {
        Objects.requireNonNull(name, "Name of the objective display position can not be null");
        for (final ObjectivePosition value : values()) {
            if (value.name().equalsIgnoreCase(name)) return Optional.of(value);
        }
        return Optional.empty();
    }

}
