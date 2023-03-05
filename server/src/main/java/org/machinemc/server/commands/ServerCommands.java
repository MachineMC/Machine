package org.machinemc.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.experimental.UtilityClass;
import org.machinemc.server.Machine;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.commands.CommandExecutor;

/**
 * Class representing default server commands.
 */
@UtilityClass
public class ServerCommands {

    /**
     * Registers all default commands for given command dispatcher using given server.
     * @param server server to register commands for
     * @param dispatcher dispatcher to register commands in
     */
    public static void register(@NotNull Machine server, @NotNull CommandDispatcher<CommandExecutor> dispatcher) {
        dispatcher.register(stopCommand(server));
    }

    /**
     * Creates the default stop command.
     * @param server server to register commands for
     * @return stop command
     */
    private static @NotNull LiteralArgumentBuilder<CommandExecutor> stopCommand(@NotNull Machine server) {
        LiteralArgumentBuilder<CommandExecutor> stopCommand = LiteralArgumentBuilder.literal("stop");
        stopCommand.executes(c -> {
            server.shutdown();
            return 0;
        });
        return stopCommand;
    }

}