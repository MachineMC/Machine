package me.pesekjak.machine.auth;

import lombok.Getter;
import me.pesekjak.machine.Machine;

import java.security.KeyPair;

/**
 * Adds functionality to Machine server in online mode
 */
public class OnlineServerImpl implements OnlineServer {

    @Getter
    private final Machine server;

    @Getter
    protected final KeyPair key;

    public OnlineServerImpl(Machine server) {
        this.server = server;
        key = Crypt.generateKeyPair();
    }

}
