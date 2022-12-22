package me.pesekjak.machine.auth;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;

/**
 * Default functionality of the online server.
 */
@Getter
public class OnlineServerImpl implements OnlineServer {

    private final @NotNull Machine server;
    protected final @NotNull KeyPair key;

    public OnlineServerImpl(@NotNull Machine server) {
        this.server = server;
        key = Crypt.generateKeyPair();
    }

}
