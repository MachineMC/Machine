package me.pesekjak.machine.auth;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Adds functionality to Machine server in online mode
 */
public class OnlineServer implements ServerProperty {

    @Getter
    private final Machine server;

    @Getter
    protected final KeyPair key;

    public OnlineServer(Machine server) {
        this.server = server;
        key = Crypt.generateKeyPair();
        if(key == null)
            throw new IllegalStateException("Key for the online server wasn't initialized");
    }

    /**
     * @return sequence of random 4 bytes used for verification
     */
    public byte[] nextVerifyToken() {
        return Crypt.nextVerifyToken();
    }

    /**
     * Creates secret key from server's private key and secret from client.
     * @param privateKey server's private key
     * @param keyBytes client's secret
     * @return secret key
     */
    public SecretKey getSecretKey(PrivateKey privateKey, byte[] keyBytes) {
        return Crypt.decryptByteToSecretKey(privateKey, keyBytes);
    }

    public byte[] digestData(String baseServerId, PublicKey publicKey, SecretKey secretKey) {
        return Crypt.digestData(baseServerId, publicKey, secretKey);
    }

}
