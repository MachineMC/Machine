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
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.MachineContainer;
import org.machinemc.server.Machine;
import org.machinemc.server.utils.BrigadierUtils;

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
        final RootCommandNode<MachineApplication> root = dispatcher.getRoot();

        final LiteralCommandNode<MachineApplication> helpCommand = helpCommand(application);
        root.addChild(helpCommand);
        root.addChild(BrigadierUtils.buildRedirect("?", helpCommand));

        root.addChild(stopCommand(application));

        root.addChild(listCommand(application));

        root.addChild(jumpCommand(application));

        root.addChild(startCommand(application));
    }

    private static LiteralCommandNode<MachineApplication> helpCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("help")
                .executes(c -> {
                    application.getTerminal().info(
                            "help — displays all available commands",
                            "stop — stops all running servers and shut down the application",
                            "list — displays list of all available and running servers",
                            "jump <name> — jumps into a console of given running server",
                            "start <name> — starts up given server container",
                            "stop <name> — shuts down given running server"
                    );
                    return 0;
                })
                .build();
    }

    private static LiteralCommandNode<MachineApplication> stopCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("stop")
                .then(RequiredArgumentBuilder.<MachineApplication, String>argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            application.getRunningServers().stream()
                                    .map(Machine::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final MachineContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.getTerminal().info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (!container.isRunning()) {
                                application.getTerminal().info("Server '" + name + "' is currently offline");
                                return 0;
                            }
                            if (container.getInstance() == null) return 0;
                            application.getTerminal().info("Shutting down '" + container.getName() + "' server");
                            container.getInstance().shutdown();
                            return 0;
                        })
                )
                .executes(c -> {
                    application.shutdown();
                    return 0;
                })
                .build();
    }

    private static LiteralCommandNode<MachineApplication> listCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("list")
                .executes(c -> {
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
                })
                .build();
    }

    private static LiteralCommandNode<MachineApplication> jumpCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("jump")
                .then(RequiredArgumentBuilder.<MachineApplication, String>argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            application.getRunningServers().stream()
                                    .map(Machine::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final MachineContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.getTerminal().info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (!container.isRunning()) {
                                application.getTerminal().info("Server '" + name + "' is currently offline");
                                return 0;
                            }
                            application.getTerminal().openServer(container);
                            return 0;
                        })
                )
                .executes(c -> {
                    application.getTerminal().info("You need to specify the server name, usage: 'jump <name>'");
                    return 0;
                })
                .build();
    }

    private static LiteralCommandNode<MachineApplication> startCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("start")
                .then(RequiredArgumentBuilder.<MachineApplication, String>argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            application.getContainers().stream()
                                    .filter(container -> !container.isRunning())
                                    .map(MachineContainer::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final MachineContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.getTerminal().info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (container.isRunning()) {
                                application.getTerminal().info("Server '" + name + "' is already running");
                                return 0;
                            }
                            if (container.getInstance() != null) {
                                application.getTerminal().info("Server '" + name + "' hasn't been shut down yet");
                                return 0;
                            }
                            application.getTerminal().openServer(container);
                            application.startContainer(container);
                            return 0;
                        })
                )
                .executes(c -> {
                    application.getTerminal().info("You need to specify the server name, usage: 'start <name>'");
                    return 0;
                })
                .build();
    }

}
