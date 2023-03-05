package org.machinemc.api.auth;

import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.NotNull;

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
    @NotNull KeyPair getKey();

    /**
     * @return sequence of random 4 bytes used for verification
     */
    default byte @NotNull [] nextVerifyToken() {
        return Crypt.nextVerifyToken();
    }

    /**
     * Creates secret key from server's private key and encrypted secret key.
     * @param privateKey server's private key
     * @param keyBytes encrypted secret key
     * @return secret key
     */
    default @NotNull SecretKey getSecretKey(@NotNull PrivateKey privateKey, byte @NotNull [] keyBytes) {
        return Crypt.decryptByteToSecretKey(privateKey, keyBytes);
    }

    /**
     * Digest authentication process.
     * @param baseServerId server id - always empty string
     * @param publicKey server's public key
     * @param secretKey secret key shared between server and client
     * @return digested data
     */
    default byte @NotNull [] digestData(@NotNull String baseServerId, @NotNull PublicKey publicKey, @NotNull SecretKey secretKey) {
        return Crypt.digestData(baseServerId, publicKey, secretKey);
    }

}
