package me.pesekjak.machine.entities;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Entity with optional custom name.
 */
public interface Nameable {

    /**
     * @return custom name of the entity
     */
    @Nullable Component getCustomName();

    /**
     * Changes the custom name of the entity
     * @param customName new custom name
     */
    void setCustomName(@Nullable Component customName);

}
