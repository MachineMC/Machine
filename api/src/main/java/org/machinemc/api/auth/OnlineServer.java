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
