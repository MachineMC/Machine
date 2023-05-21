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
package org.machinemc.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.machinemc.api.logging.Console;
import org.machinemc.server.Machine;
import org.machinemc.api.commands.CommandExecutor;

/**
 * Class representing default server commands.
 */
public final class ServerCommands {

    private ServerCommands() {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers all default commands for given command dispatcher using given server.
     * @param server server to register commands for
     * @param dispatcher dispatcher to register commands in
     */
    public static void register(final Machine server, final CommandDispatcher<CommandExecutor> dispatcher) {
        dispatcher.register(stopCommand(server));
        dispatcher.register(exitCommand(server));
    }

    /**
     * Creates the default stop command.
     * @param server server to register commands for
     * @return stop command
     */
    private static LiteralArgumentBuilder<CommandExecutor> stopCommand(final Machine server) {
        final LiteralArgumentBuilder<CommandExecutor> stopCommand = LiteralArgumentBuilder.literal("stop");
        stopCommand.executes(c -> {
            server.shutdown();
            return 0;
        });
        return stopCommand;
    }

    /**
     * Creates the default exit command.
     * @param server server to register commands for
     * @return exit command
     */
    private static LiteralArgumentBuilder<CommandExecutor> exitCommand(final Machine server) {
        final LiteralArgumentBuilder<CommandExecutor> exitCommand = LiteralArgumentBuilder.literal("exit");
        exitCommand.executes(c -> {
            if (!(c.getSource() instanceof Console)) return -1;
            server.getApplication().exitServer(server);
            return 0;
        });
        return exitCommand;
    }

}
