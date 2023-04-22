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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.auth;

import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.api.auth.Crypt;
import org.machinemc.api.auth.OnlineServer;

import java.security.KeyPair;

/**
 * Default functionality of the online server.
 */
@Getter
public class OnlineServerImpl implements OnlineServer {

    private final Machine server;
    protected final KeyPair key;

    public OnlineServerImpl(final Machine server) {
        this.server = server;
        key = Crypt.generateKeyPair();
    }

}
