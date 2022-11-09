package me.pesekjak.machine.entities;

import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;

public enum EntityType {

    PLAYER(116, 0.6, 1.8, "player", Player.class);

    @Getter
    private final int ID;
    @Getter
    private final double width;
    @Getter
    private final double height;
    @Getter
    private final NamespacedKey identifier;
    @Getter
    private final Class<? extends Entity> entityClass;
    @Getter
    private final String typeName;

    EntityType(int id, double width, double height, String name, Class<? extends Entity> entityClass) {
        ID = id;
        this.width = width;
        this.height = height;
        this.identifier = NamespacedKey.minecraft(name);
        this.entityClass = entityClass;
        this.typeName = name.replace('_', ' ');
    }

}
