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
package org.machinemc.application.terminal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.MachineContainer;
import org.machinemc.server.Machine;

import java.util.List;

/**
 * Class with default application commands.
 */
public final class ApplicationCommands {

    private ApplicationCommands() {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers all default commands for given command dispatcher using given server.
     * @param application application to register commands for
     * @param dispatcher dispatcher to register commands in
     */
    public static void register(final MachineApplication application,
                                final CommandDispatcher<MachineApplication> dispatcher) {
        dispatcher.register(stopCommand(application));
        dispatcher.register(listCommand(application));
    }

    /**
     * Creates the default stop command.
     * @param application application to register commands for
     * @return stop command
     */
    private static LiteralArgumentBuilder<MachineApplication> stopCommand(final MachineApplication application) {
        final LiteralArgumentBuilder<MachineApplication> stopCommand = LiteralArgumentBuilder.literal("stop");
        stopCommand.executes(c -> {
            application.shutdown();
            return 0;
        });
        return stopCommand;
    }

    /**
     * Creates the default list command.
     * @param application application to register commands for
     * @return list command
     */
    private static LiteralArgumentBuilder<MachineApplication> listCommand(final MachineApplication application) {
        final LiteralArgumentBuilder<MachineApplication> listCommand = LiteralArgumentBuilder.literal("list");
        listCommand.executes(c -> {

            final List<MachineContainer> containers = application.getContainers();
            StringBuilder formatted = new StringBuilder();
            formatted.append("Available server containers (")
                    .append(containers.size())
                    .append("): ");
            for (int i = 0; i < containers.size(); i++) {
                formatted.append(containers.get(0).getName());
                if (i != containers.size() - 1) formatted.append(", ");
            }
            application.getTerminal().info(formatted.toString());

            final List<Machine> servers = application.getRunningServers();
            formatted = new StringBuilder();
            formatted.append("Running servers (")
                    .append(servers.size())
                    .append("): ");
            for (int i = 0; i < servers.size(); i++) {
                formatted.append(servers.get(0).getName());
                if (i != servers.size() - 1) formatted.append(", ");
            }
            application.getTerminal().info(formatted.toString());

            return 0;
        });
        return listCommand;
    }

}
