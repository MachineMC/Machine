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
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
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
        final RootCommandNode<CommandExecutor> root = dispatcher.getRoot();
        root.addChild(stopCommand(server));
        root.addChild(exitCommand(server));
    }

    /**
     * Creates the default stop command.
     * @param server server to register commands for
     * @return stop command
     */
    private static LiteralCommandNode<CommandExecutor> stopCommand(final Machine server) {
        return LiteralArgumentBuilder
                .<CommandExecutor>literal("stop")
                .executes(c -> {
                    server.shutdown();
                    return 0;
                })
                .build();
    }

    /**
     * Creates the default exit command.
     * @param server server to register commands for
     * @return exit command
     */
    private static LiteralCommandNode<CommandExecutor> exitCommand(final Machine server) {
        return LiteralArgumentBuilder
                .<CommandExecutor>literal("exit")
                .executes(c -> {
                    if (!(c.getSource() instanceof Console)) return -1;
                    server.getApplication().exitServer(server);
                    return 0;
                })
                .build();
    }

}
