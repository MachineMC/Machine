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
package org.machinemc.api.auth;

import lombok.Getter;
import org.machinemc.api.Server;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Class that adds functionality to the server in online mode.
 */
@Getter
public class OnlineServer {

    private final Server server;
    protected final KeyPair key;

    public OnlineServer(final Server server) {
        this.server = server;
        key = Crypt.generateKeyPair();
    }

    /**
     * @return sequence of random 4 bytes used for verification
     */
    public byte[] nextVerifyToken() {
        return Crypt.nextVerifyToken();
    }

    /**
     * Creates secret key from server's private key and encrypted secret key.
     * @param privateKey server's private key
     * @param keyBytes encrypted secret key
     * @return secret key
     */
    public SecretKey getSecretKey(final PrivateKey privateKey, final byte[] keyBytes) {
        return Crypt.decryptByteToSecretKey(privateKey, keyBytes);
    }

    /**
     * Digest authentication process.
     * @param baseServerID server id - always empty string
     * @param publicKey server's public key
     * @param secretKey secret key shared between server and client
     * @return digested data
     */
    public byte[] digestData(final String baseServerID, final PublicKey publicKey, final SecretKey secretKey) {
        return Crypt.digestData(baseServerID, publicKey, secretKey);
    }

    @Override
    public String toString() {
        return "OnlineServer";
    }

}
