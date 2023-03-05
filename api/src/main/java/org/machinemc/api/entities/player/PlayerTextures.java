package org.machinemc.api.entities.player;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * Represents player's skin textures.
 */
public interface PlayerTextures extends Writable {

    /**
     * @return base64 texture value of the skin
     */
    String value();

    /**
     * @return signature of the skin
     */
    @Nullable String signature();

    /**
     * @return URL of the skin texture
     */
    URL skinUrl();

    /**
     * @return URL of the texture of skin's cape
     */
    @Nullable URL capeUrl();

    /**
     * @return type of the skin model
     */
    SkinModel skinModel();

    default void write(ServerBuffer buf) {
        buf.writeTextures(this);
    }

}
