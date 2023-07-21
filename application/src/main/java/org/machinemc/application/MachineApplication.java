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
package org.machinemc.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.application.terminal.ApplicationCommands;
import org.machinemc.application.terminal.ApplicationTerminal;
import org.machinemc.application.terminal.TerminalFactory;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.MachinePlatform;
import org.machinemc.server.logging.DynamicConsole;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Implementation of server application for Machine server.
 */
public final class MachineApplication implements ServerApplication {

    public static final String DEFAULT_SERVER = "machine-server";

    /**
     * Whether the application is running.
     */
    @Getter
    private boolean running;

    /**
     * Main directory of the application.
     */
    @Getter
    private final File directory;

    @Getter
    private final Set<Argument> arguments;

    /**
     * Application terminal, provides console implementations for running servers.
     */
    @Getter
    private final ApplicationTerminal terminal;

    @Getter
    private final ServerManager serverManager;

    @Getter
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Command dispatcher for application terminal.
     */
    @Getter
    private final CommandDispatcher<MachineApplication> commandDispatcher;

    /**
     * Main scheduler of the application.
     */
    @Getter
    private final Scheduler scheduler;

    /**
     * Default Machine server platform.
     */
    private final MachinePlatform machinePlatform = new MachinePlatform();

    private final Map<String, ServerPlatform> platforms = new TreeMap<>();

    private MachineApplication(final String[] args) {
        directory = new File(".");

        arguments = Argument.parse(args);

        try {
            terminal = TerminalFactory.create(this).build();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initialize application console");
        }

        commandDispatcher = new CommandDispatcher<>();
        ApplicationCommands.register(this, commandDispatcher);

        scheduler = new Scheduler(4);

        try {
            serverManager = new ServerManager(this);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initialize server manager");
        }
    }

    /**
     * Application entry point.
     * @param args java arguments
     */
    public static void main(final String[] args) {
        MachineApplication application = null;
        try {
            application = new MachineApplication(args);
            application.run();
        } catch (Exception exception) {
            if (application != null)
                application.handleException(exception);
            else
                exception.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Starts Machine Application.
     */
    private void run() {

        running = true;

        final long start = System.currentTimeMillis();

        info("Loading Machine Application...");

        info("Loading server platforms...");

        final List<ServerPlatform> platforms = new ArrayList<>();
        platforms.add(machinePlatform);

        for (final ServerPlatform platform : platforms) {
            info("Loading '" + platform.getCodeName() + "' platform");
            try {
                if (loadPlatform(platform)) continue;
                severe("Failed to load '" + platform.getCodeName() + "', perhaps another platform with the same "
                        + "code name already exists?");
            } catch (Exception exception) {
                handleException(exception);
            }
        }

        try {
            serverManager.readFile();
            serverManager.updateFile();
        } catch (Exception exception) {
            handleException(exception);
            shutdown();
        }

        info("Welcome to Machine! (loaded in " + (System.currentTimeMillis() - start) + "ms)");
        info("Use command 'help' or '?' to display available commands");

        terminal.start();

        if (getContainers().size() == 0) {
            info("No server container is available, creating default '" + DEFAULT_SERVER + "' server");
            final ServerContainer container = new ServerContainer(new File(DEFAULT_SERVER + "/"), machinePlatform);
            serverManager.loadContainer(container);
            try {
                serverManager.updateFile();
            } catch (Exception exception) {
                handleException(exception);
            }
        }

        if (getContainers().size() == 1) {
            final ServerContainer container = getContainers().iterator().next();
            terminal.openServer(container);
            info("Only one server found, automatically launching '" + container.getName() + "'");
            info("Use command 'exit' to return to the main application console");
            startContainer(container);
        } else {
            terminal.openServer(null);
        }
    }

    /**
     * Starts server from a container, if the container has no active
     * instance, new one is created.
     * @param container container
     */
    public void startContainer(final ServerContainer container) {
        final RunnableServer server;
        try {
            if (container.getInstance().isPresent())
                throw new RuntimeException("Server container '" + container.getName() + "' has been already initiated");
            final DynamicConsole console = terminal.createConsole(container);
            server = container.getPlatform().create(new ServerContext(
                    this,
                    container.getDirectory(),
                    container.getName(),
                    console,
                    container.getPlatform()
            ));
            console.setSource(server);
            container.setInstance(server);
        } catch (Exception exception) {
            stopServer(container);
            handleException(exception);
            return;
        }
        info("Starting up '" + container.getName() + "' server");
        Scheduler.task((input, session) -> {
            try {
                server.run();
            } catch (Exception exception) {
                stopServer(server);
                handleException(exception);
            }
            return null;
        }).async().run(scheduler);
    }

    /**
     * @return all loaded server platforms
     */
    public @Unmodifiable Collection<ServerPlatform> loadedPlatforms() {
        return Collections.unmodifiableCollection(platforms.values());
    }

    /**
     * Returns server platform with given name.
     * @param codename code name of the platform
     * @return platform
     */
    public Optional<ServerPlatform> getPlatform(final String codename) {
        return Optional.ofNullable(platforms.get(codename.toLowerCase()));
    }

    /**
     * Loads a new server platform.
     * @param platform platform to load
     * @return true if the platform has been loaded successfully
     */
    public boolean loadPlatform(final ServerPlatform platform) {
        if (platforms.containsKey(platform.getCodeName().toLowerCase())) return false;
        platforms.put(platform.getCodeName().toLowerCase(), platform);
        platform.load(this);
        info("Platform '" + platform.getCodeName() + "' has been loaded");
        return true;
    }

    /**
     * Unloads a loaded server platform, shutdowns, and removes all running containers with
     * given platform.
     * @param platform platform to unload
     */
    public void unloadPlatform(final ServerPlatform platform) {
        if (platforms.get(platform.getCodeName().toLowerCase()) != platform) return;
        platforms.remove(platform.getCodeName().toLowerCase());
        for (final ServerContainer container : getContainers()) {
            if (container.getPlatform() != platform) continue;
            serverManager.unloadContainer(container);
        }
        info("Platform '" + platform.getCodeName() + "' has been unloaded");
    }

    /**
     * Returns unmodifiable list of all available Machine containers.
     * @return all containers
     */
    public @Unmodifiable Collection<ServerContainer> getContainers() {
        return serverManager.getContainers();
    }

    /**
     * Returns list of all running server instances.
     * @return all running servers
     */
    public @Unmodifiable List<RunnableServer> getRunningServers() {
        return getContainers().stream()
                .map(ServerContainer::getInstance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(RunnableServer::isRunning)
                .collect(Collectors.toList());
    }

    /**
     * Returns server container for provided server instance.
     * @param server server instance of the container
     * @return container for given server
     * @throws IllegalArgumentException if the provided server has no container
     */
    public ServerContainer container(final RunnableServer server) {
        if (server == null) throw new NullPointerException();
        for (final ServerContainer container : getContainers()) {
            if (container.getDirectory().equals(server.getDirectory()))
                return container;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns server container for provided directory.
     * @param directory directory of the container
     * @return container for given directory
     * @throws IllegalArgumentException if the provided directory has no container
     */
    public ServerContainer container(final File directory) {
        if (directory == null) throw new NullPointerException();
        for (final ServerContainer container : getContainers()) {
            if (container.getDirectory().equals(directory))
                return container;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns server container for provided directory.
     * @param name name of the container
     * @return container for given name
     * @throws IllegalArgumentException if there is no container with such name
     */
    public ServerContainer container(final String name) {
        if (name == null) throw new NullPointerException();
        for (final ServerContainer container : getContainers()) {
            if (container.getName().equals(name))
                return container;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void exitServer(final RunnableServer server) {
        exitServer(container(server));
    }

    /**
     * Exits from an active server container.
     * @param container container to exit from
     */
    public void exitServer(final ServerContainer container) {
        terminal.exitServer(container);
    }

    @Override
    public void stopServer(final RunnableServer server) {
        stopServer(container(server));
    }

    /**
     * Stops server container.
     * @param container container to stop
     */
    public void stopServer(final ServerContainer container) {
        if (container.isRunning()) {
            try {
                container.getInstance()
                        .orElseThrow(() ->
                                new IllegalArgumentException("Server '" + container.getName() + "' is offline"))
                        .shutdown();
            } catch (Exception exception) {
                handleException(exception);
            }
            return;
        }
        info("Server '" + container.getName() + "' has been shut down");
        terminal.exitServer(container);
        container.setInstance(null);
    }

    @Override
    public void log(final Level level, final String... messages) {
        terminal.log(null, level, messages);
    }

    @Override
    public void sendMessage(final @Nullable UUID sender, final Component message, final MessageType type) {
        terminal.sendMessage(null, sender, message, type);
    }

    /**
     * Shutdowns the application.
     */
    public void shutdown() {
        info("Shutting down...");
        for (final ServerContainer container : getContainers()) {
            if (container.getInstance().isEmpty()) continue;
            info("Shutting down '" + container.getName() + "' server");
            try {
                container.getInstance().get().shutdown();
            } catch (Exception exception) {
                handleException(exception);
            }
        }
        info("Machine has been shut down");
        running = false;
        System.exit(0);
    }

    /**
     * Handles generated exception.
     * @param throwable exception to handle
     */
    public void handleException(final Throwable throwable) {
        handleException(throwable, null);
    }

    /**
     * Handles generated exception.
     * @param throwable exception to handle
     * @param reason reason of the exception being thrown
     */
    public void handleException(final Throwable throwable, final @Nullable String reason) {
        Throwable initialCause = throwable;
        while (initialCause.getCause() != null)
            initialCause = initialCause.getCause();
        terminal.severe("Application generated " + initialCause.getClass().getName(),
                "Reason: " + (reason != null ? reason : initialCause.getMessage()),
                "Stack trace: ");
        terminal.severe(Arrays.stream(initialCause.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

}
