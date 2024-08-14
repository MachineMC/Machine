/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.client.resourcepack;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.With;
import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.components.Component;

import java.util.function.BiConsumer;

/**
 * Resource pack download request for the client.
 *
 * @param pack resource pack to download
 * @param forced the Notchian client will be forced to use the resource pack from the server,
 *               if they decline they will be kicked from the server
 * @param prompt this is shown in the prompt making the client accept or decline the resource pack
 */
@Builder
public record ResourcePackRequest(ResourcePackInfo pack, boolean forced, @With @Nullable Component prompt) {

    public ResourcePackRequest {
        Preconditions.checkNotNull(pack, "Pack can not be null");
    }

    /**
     * A callback for a resource pack application operation.
     */
    @FunctionalInterface
    public interface Callback {

        /**
         * Creates a pack callback that will only execute the provided functions
         * when the pack application has completed, discarding all intermediate events.
         *
         * @param success action on successfully loading the resource pack
         * @param failure action on failing
         * @return callback
         */
        static Callback onTerminal(BiConsumer<ResourcePackInfo, ResourcePackReceiver> success,
                                   BiConsumer<ResourcePackInfo, ResourcePackReceiver> failure) {
            Preconditions.checkNotNull(success, "Success consumer can not be null");
            Preconditions.checkNotNull(failure, "Failure consumer can not be null");
            return (pack, status, receiver) -> {
                switch (status) {
                    case ACCEPTED, DOWNLOADED -> { }
                    case DECLINED, DISCARDED, FAILED_DOWNLOAD, FAILED_RELOAD, INVALID_URL -> failure.accept(pack, receiver);
                    case SUCCESSFULLY_LOADED -> success.accept(pack, receiver);
                }
            };
        }

        /**
         * Called when resource pack receiver responds to a resource pack
         * request.
         *
         * @param pack requested resource pack
         * @param status response
         * @param receiver entity who responded
         */
        void onResponse(ResourcePackInfo pack, ResourcePackStatus status, ResourcePackReceiver receiver);

    }

}
