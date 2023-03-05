package org.machinemc.api.entities;

import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.entities.player.Hand;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.api.entities.player.SkinPart;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.*;

import java.util.Set;

/**
 * Represents a human entity, such as an NPC or a player.
 */
public interface HumanEntity extends LivingEntity {

    /**
     * @return profile of the human
     */
    @NotNull PlayerProfile getProfile();

    /**
     * @return username of the profile of the human
     */
    default @NonNls @NotNull String getUsername() {
        return getProfile().getUsername();
    }

    /**
     * @return gamemode of the human
     */
    @NotNull Gamemode getGamemode();

    /**
     * Changes gamemode of the human.
     * @param gamemode new gamemode
     */
    void setGamemode(@NotNull Gamemode gamemode);

    /**
     * @return unmodifiable set of enabled skin parts of the human's skin
     */
    @Unmodifiable @NotNull Set<SkinPart> getDisplayedSkinParts();

    /**
     * @return main hand of the human
     */
    @NotNull Hand getMainHand();

    /**
     * @return display name of the human
     */
    @NotNull Component getDisplayName();

    /**
     * Changes the display name of the human.
     * @param displayName new display name
     */
    void setDisplayName(@Nullable Component displayName);

    /**
     * @return name of the human in the player list
     */
    @NotNull Component getPlayerListName();

    /**
     * Changes the player list name of the human.
     * @param playerListName new player list name
     */
    void setPlayerListName(@Nullable Component playerListName);

}
