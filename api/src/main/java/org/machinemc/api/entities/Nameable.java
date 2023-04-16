package org.machinemc.api.entities;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.components.Component;

/**
 * Entity with optional custom name.
 */
public interface Nameable {

    /**
     * @return custom name of the entity
     */
    @Nullable Component getCustomName();

    /**
     * Changes the custom name of the entity.
     * @param customName new custom name
     */
    void setCustomName(@Nullable Component customName);

}
