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
import org.machinemc.application.RunnableServer;
import org.machinemc.application.ServerContainer;
import org.machinemc.application.ServerPlatform;
import org.machinemc.server.utils.BrigadierUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
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

        root.addChild(restartCommand(application));

        root.addChild(createCommand(application));
    }

    private static LiteralCommandNode<MachineApplication> helpCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("help")
                .executes(c -> {
                    application.info(
                            "help — displays all available commands",
                            "stop — stops all running servers and shut down the application",
                            "list — displays list of all available and running servers",
                            "jump <name> — jumps into a console of given running server",
                            "start <name> — starts up given server container",
                            "stop <name> — shuts down given running server",
                            "restart <name> — restarts given running server",
                            "create <name> <platform> — creates new server instance"
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
                                    .map(RunnableServer::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final ServerContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (!container.isRunning()) {
                                application.info("Server '" + name + "' is currently offline");
                                return 0;
                            }
                            if (container.getInstance() == null) return 0;
                            application.stopServer(container);
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
                    final Collection<ServerContainer> containers = application.getContainers();
                    StringBuilder formatted = new StringBuilder();
                    formatted.append("Available server containers (")
                            .append(containers.size())
                            .append("): ");
                    final Iterator<ServerContainer> iterator = containers.iterator();
                    while (iterator.hasNext()) {
                        formatted.append(iterator.next().getName());
                        if (iterator.hasNext()) formatted.append(", ");
                    }
                    application.info(formatted.toString());

                    final List<RunnableServer> servers = application.getRunningServers();
                    formatted = new StringBuilder();
                    formatted.append("Running servers (")
                            .append(servers.size())
                            .append("): ");
                    for (int i = 0; i < servers.size(); i++) {
                        formatted.append(servers.get(0).getName());
                        if (i != servers.size() - 1) formatted.append(", ");
                    }
                    application.info(formatted.toString());

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
                                    .map(RunnableServer::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final ServerContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (!container.isRunning()) {
                                application.info("Server '" + name + "' is currently offline");
                                return 0;
                            }
                            application.getTerminal().openServer(container);
                            return 0;
                        })
                )
                .executes(c -> {
                    application.info("You need to specify the server name, usage: 'jump <name>'");
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
                                    .map(ServerContainer::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final ServerContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (container.isRunning()) {
                                application.info("Server '" + name + "' is already running");
                                return 0;
                            }
                            if (container.getInstance() != null) {
                                application.info("Server '" + name + "' hasn't been shut down yet");
                                return 0;
                            }
                            application.getTerminal().openServer(container);
                            application.startContainer(container);
                            return 0;
                        })
                )
                .executes(c -> {
                    application.info("You need to specify the server name, usage: 'start <name>'");
                    return 0;
                })
                .build();
    }

    private static LiteralCommandNode<MachineApplication> restartCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("restart")
                .then(RequiredArgumentBuilder.<MachineApplication, String>argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            application.getContainers().stream()
                                    .filter(ServerContainer::isRunning)
                                    .map(ServerContainer::getName)
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            final String name = ctx.getArgument("name", String.class);
                            final ServerContainer container;
                            try {
                                container = application.container(name);
                            } catch (Exception exception) {
                                application.info("There is no server with name '" + name + "'");
                                return 0;
                            }
                            if (!container.isRunning()) {
                                application.info("Server '" + name + "' is currently offline");
                                return 0;
                            }
                            if (container.getInstance() == null) return 0;
                            application.info("Restarting server '" + name + "'...");
                            application.stopServer(container);
                            application.getTerminal().openServer(container);
                            application.startContainer(container);
                            return 0;
                        })
                )
                .executes(c -> {
                    application.info("You need to specify the server name, usage: 'restart <name>'");
                    return 0;
                })
                .build();
    }

    private static LiteralCommandNode<MachineApplication> createCommand(final MachineApplication application) {
        return LiteralArgumentBuilder
                .<MachineApplication>literal("create")
                .then(RequiredArgumentBuilder.<MachineApplication, String>argument("name", StringArgumentType.word())
                        .then(RequiredArgumentBuilder.<MachineApplication, String>argument(
                                "platform",
                                StringArgumentType.word()
                                )
                                .suggests((ctx, builder) -> {
                                    application.loadedPlatforms().stream()
                                            .map(ServerPlatform::getCodeName)
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    final String name = ctx.getArgument("name", String.class);

                                    if (application.getContainers().stream()
                                            .anyMatch(container -> container.getDirectory().getName().equals(name))) {
                                        application.info("Server with name '" + name + "' already exists");
                                        return 0;
                                    }

                                    final String platformName = ctx.getArgument("platform", String.class);
                                    final ServerPlatform platform = application.getPlatform(platformName);

                                    if (platform == null) {
                                        application.info("Platform '" + platformName + "' does not exist");
                                        return 0;
                                    }

                                    final ServerContainer container = new ServerContainer(
                                            new File(application.getDirectory(), name),
                                            platform
                                    );
                                    application.getServerManager().loadContainer(container);
                                    application.info("Created new '"
                                            + platformName
                                            + "' server '"
                                            + name
                                            + "'");
                                    try {
                                        application.getServerManager().updateFile();
                                    } catch (IOException exception) {
                                        throw new RuntimeException(exception);
                                    }

                                    return 0;
                                })
                        )
                        .executes(ctx -> {
                            application.info("You need to specify the server platform, "
                                    + "usage: 'create <name> <platform>'");
                            return 0;
                        })
                )
                .executes(c -> {
                    application.info("You need to specify the server name, usage: 'create <name> <platform>'");
                    return 0;
                })
                .build();
    }

}
