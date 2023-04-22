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
package org.machinemc.api.auth;

import org.machinemc.api.server.ServerProperty;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Class that adds functionality to the server in online mode.
 */
public interface OnlineServer extends ServerProperty {

    /**
     * @return server's key
     */
    KeyPair getKey();

    /**
     * @return sequence of random 4 bytes used for verification
     */
    default byte[] nextVerifyToken() {
        return Crypt.nextVerifyToken();
    }

    /**
     * Creates secret key from server's private key and encrypted secret key.
     * @param privateKey server's private key
     * @param keyBytes encrypted secret key
     * @return secret key
     */
    default SecretKey getSecretKey(PrivateKey privateKey, byte[] keyBytes) {
        return Crypt.decryptByteToSecretKey(privateKey, keyBytes);
    }

    /**
     * Digest authentication process.
     * @param baseServerId server id - always empty string
     * @param publicKey server's public key
     * @param secretKey secret key shared between server and client
     * @return digested data
     */
    default byte[] digestData(String baseServerId, PublicKey publicKey, SecretKey secretKey) {
        return Crypt.digestData(baseServerId, publicKey, secretKey);
    }

}
