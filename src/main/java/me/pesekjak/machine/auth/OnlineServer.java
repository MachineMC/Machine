package me.pesekjak.machine.auth;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

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

    public byte[] nextVerifyToken() {
        return Crypt.nextVerifyToken();
    }

    public SecretKey getSecretKey(PrivateKey privateKey, byte[] keyBytes) {
        return Crypt.decryptByteToSecretKey(privateKey, keyBytes);
    }

    public byte[] digestData(String baseServerId, PublicKey publicKey, SecretKey secretKey) {
        return Crypt.digestData(baseServerId, publicKey, secretKey);
    }

}
