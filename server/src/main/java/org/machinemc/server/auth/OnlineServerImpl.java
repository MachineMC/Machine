package org.machinemc.server.auth;

import lombok.Getter;
import org.machinemc.server.Machine;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.auth.Crypt;
import org.machinemc.api.auth.OnlineServer;

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
