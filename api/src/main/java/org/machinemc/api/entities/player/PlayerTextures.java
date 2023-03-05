package org.machinemc.api.entities.player;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * Represents player's skin textures.
 */
public interface PlayerTextures extends Writable {

    /**
     * @return base64 texture value of the skin
     */
    @NonNls @NotNull String value();

    /**
     * @return signature of the skin
     */
    @NonNls @Nullable String signature();

    /**
     * @return URL of the skin texture
     */
    @NotNull URL skinUrl();

    /**
     * @return URL of the texture of skin's cape
     */
    @Nullable URL capeUrl();

    /**
     * @return type of the skin model
     */
    @NotNull SkinModel skinModel();

    default void write(@NotNull ServerBuffer buf) {
        buf.writeTextures(this);
    }

}
