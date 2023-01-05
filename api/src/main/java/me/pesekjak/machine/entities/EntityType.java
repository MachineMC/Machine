package me.pesekjak.machine.entities;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.pesekjak.machine.utils.NamespacedKey.KEY_REGEX;

/**
 * Represents entity type (model) of the entity.
 */
@Getter
public enum EntityType {

    PLAYER(116, 0.6, 1.8, "player");

    private final int id;
    private final double width;
    private final double height;
    private final @NotNull NamespacedKey identifier;
    private final @NotNull String typeName;

    EntityType(int id, double width, double height, @NotNull @Pattern(KEY_REGEX) String name) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.identifier = NamespacedKey.minecraft(name);
        this.typeName = name.replace('_', ' ');
    }

    /**
     * Returns entity type from its id.
     * @param id id of the entity type
     * @return entity type for given id
     */
    public static @NotNull EntityType fromID(int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Entity type");
        return values()[id];
    }

    /**
     * Returns entity type of given name.
     * @param name name of the entity type
     * @return entity type with given name
     */
    public static @Nullable EntityType getByName(@NotNull String name) {
        for (EntityType value : values()) {
            if (value.name().equalsIgnoreCase(name) ||
                    value.identifier.key().equalsIgnoreCase(name) ||
                    value.typeName.equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
