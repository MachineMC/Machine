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
package org.machinemc.client.cookie;

import com.google.common.base.Preconditions;
import org.machinemc.barebones.key.NamespacedKey;

/**
 * Stored information that persists between server transfers.
 * <p>
 * The Notchian client only accepts cookies of up to 5 kiB in size.
 *
 * @param key key of the cookie
 * @param payload cookie data
 */
public record Cookie(NamespacedKey key, byte[] payload) {

    public Cookie {
        Preconditions.checkNotNull(key, "Cookie key can not be null");
        Preconditions.checkNotNull(payload, "Cookie payload can not be null");
        Preconditions.checkState(payload.length <= 5120, "Only cookies of up to 5 kiB in size are accepted");
    }

}
