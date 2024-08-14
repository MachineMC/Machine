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
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.UUID;

/**
 * Contains information about a resource pack.
 *
 * @param id id of the resource pack
 * @param uri address for downloading the resource pack
 * @param hash A 40 character hexadecimal, case-insensitive SHA-1 hash of the resource pack file.
 *             If it's not a 40 character hexadecimal string, the client will not use it for hash
 *             verification and likely waste bandwidth.
 */
@Builder
public record ResourcePackInfo(UUID id, URI uri, @Nullable String hash) {

    public ResourcePackInfo {
        Preconditions.checkNotNull(id, "Resource pack ID can not be null");
        Preconditions.checkNotNull(uri, "Resource pack URI can not be null");
    }

    /**
     * Whether the resource pack has a valid hash that can be used
     * by the client for verification of already downloaded resource packs.
     *
     * @return whether the resource pack has a valid hash
     */
    public boolean validHash() {
        if (hash == null) return false;
        return hash.length() == 40;
    }

}
