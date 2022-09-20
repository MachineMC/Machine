package me.pesekjak.machine.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;

@AllArgsConstructor
public enum EntityType {

    PLAYER(116, 0.6, 1.8, NamespacedKey.minecraft("player"), Player.class);

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

}
