package org.machinemc.api.entities;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;

/**
 * Represents entity type (model) of the entity.
 */
@Getter
public enum EntityType {

    PLAYER(116, 0.6, 1.8, "player");

    private final int id;
    private final double width;
    private final double height;
    private final NamespacedKey identifier;
    private final String typeName;

    EntityType(int id, double width, double height, String name) {
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
    public static EntityType fromID(int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Entity type");
        return values()[id];
    }

    /**
     * Returns entity type of given name.
     * @param name name of the entity type
     * @return entity type with given name
     */
    public static @Nullable EntityType getByName(String name) {
        for (EntityType value : values()) {
            if (value.name().equalsIgnoreCase(name) ||
                    value.identifier.getKey().equalsIgnoreCase(name) ||
                    value.typeName.equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

}
