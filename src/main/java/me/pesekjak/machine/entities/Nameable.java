package me.pesekjak.machine.entities;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public interface Nameable {

    @Nullable
    Component getCustomName();

    void setCustomName(@Nullable Component customName);

}
