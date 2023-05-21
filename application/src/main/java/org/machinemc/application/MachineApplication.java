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

import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.Material;
import org.machinemc.application.terminal.ApplicationCommands;
import org.machinemc.application.terminal.ApplicationTerminal;
import org.machinemc.application.terminal.TerminalFactory;
import org.machinemc.server.Machine;
import org.machinemc.server.Server;
import org.machinemc.server.ServerApplication;
import org.machinemc.server.file.ServerPropertiesImpl;
import org.machinemc.server.network.packets.PacketFactory;
import org.machinemc.server.utils.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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

    /**
     * Application terminal, provides console implementations for running servers.
     */
    @Getter
    private final ApplicationTerminal terminal;

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
     * List of all available Machine containers.
     */
    private final List<MachineContainer> containers = new LinkedList<>();

    private MachineApplication() throws IOException {
        directory = new File("");

        terminal = TerminalFactory.create(this).build();

        commandDispatcher = new CommandDispatcher<>();
        ApplicationCommands.register(this, commandDispatcher);

        scheduler = new Scheduler(4);
    }

    /**
     * Application entry point.
     * @param args java arguments
     */
    public static void main(final String[] args) throws Exception {
        final MachineApplication application = new MachineApplication();
        try {
            application.run();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Starts Machine Application.
     */
    private void run() {

        terminal.start();
        running = true;

        final long start = System.currentTimeMillis();

        terminal.info("Loading Machine Application...");

        Arrays.stream(Material.values()).forEach(Material::createBlockData);
        BlockData.finishRegistration();
        terminal.info("Loaded materials and block data");

        ClassUtils.loadClass(PacketFactory.class);
        terminal.info("Loaded all packet mappings");

        terminal.info("Welcome to Machine! (loaded in " + (System.currentTimeMillis() - start) + "ms)");
        terminal.info("Use command 'help' or '?' to display available commands");

        try {
            scanServers().forEach(dir -> containers.add(new MachineContainer(dir)));
        } catch (Exception exception) {
            handleException(exception);
        }

        if (containers.size() == 0) {
            terminal.info("No server container is available, creating default '" + DEFAULT_SERVER + "' server");
            final MachineContainer container = new MachineContainer(new File(DEFAULT_SERVER + "/"));
            containers.add(container);
        }

        if (containers.size() == 1) {
            final MachineContainer container = containers.get(0);
            terminal.openServer(container);
            terminal.info("Only one server found, automatically launching '" + container.getName() + "'");
            terminal.info("Use command 'exit' to return to the main application console");
            startContainer(container);
        }
    }

    /**
     * Starts server from a container, if the container has no active
     * instance, new one is created.
     * @param container container
     */
    public void startContainer(final MachineContainer container) {
        final Machine server;
        try {
            if (container.getInstance() != null)
                throw new RuntimeException("Server container '" + container.getName() + "' has been already initiated");
            server = new Machine(
                    this,
                    container.getDirectory(),
                    container.getName(),
                    terminal.createConsole(container)
            );
            container.setInstance(server);
        } catch (Exception exception) {
            stopServer(container);
            handleException(exception);
            return;
        }
        terminal.info("Starting up '" + container.getName() + "' server");
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
     * Scans for all available servers directories inside of the main
     * application directory.
     * @return all available servers
     */
    public @Unmodifiable List<File> scanServers() throws IOException {
        final List<File> directories = new ArrayList<>();
        for (final Path path : Files.walk(directory.toPath(), 2).collect(Collectors.toSet())) {
            if (!path.endsWith(ServerPropertiesImpl.PROPERTIES_FILE_NAME)) continue;
            if (path.getNameCount() != 2) continue;
            directories.add(path.getParent().toFile());
        }
        return Collections.unmodifiableList(directories);
    }

    /**
     * Returns unmodifiable list of all available Machine containers.
     * @return all containers
     */
    public @Unmodifiable List<MachineContainer> getContainers() {
        return Collections.unmodifiableList(containers);
    }

    /**
     * Returns list of all running server instances.
     * @return all running servers
     */
    public @Unmodifiable List<Machine> getRunningServers() {
        return containers.stream()
                .map(MachineContainer::getInstance)
                .filter(Objects::nonNull)
                .filter(Machine::isRunning)
                .collect(Collectors.toList());
    }

    /**
     * Returns server container for provided server instance.
     * @param server server instance of the container
     * @return container for given server
     * @throws IllegalArgumentException if the provided server has no container
     */
    public MachineContainer container(final Server server) {
        if (server == null) throw new NullPointerException();
        for (final MachineContainer container : containers) {
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
    public MachineContainer container(final File directory) {
        if (directory == null) throw new NullPointerException();
        for (final MachineContainer container : containers) {
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
    public MachineContainer container(final String name) {
        if (name == null) throw new NullPointerException();
        for (final MachineContainer container : containers) {
            if (container.getName().equals(name))
                return container;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void exitServer(final Machine server) {
        exitServer(container(server));
    }

    /**
     * Exits from an active server container.
     * @param container container to exit from
     */
    public void exitServer(final MachineContainer container) {
        terminal.exitServer(container);
        terminal.openServer(null);
    }

    @Override
    public void stopServer(final Machine server) {
        stopServer(container(server));
    }

    /**
     * Stops server container.
     * @param container container to stop
     */
    public void stopServer(final MachineContainer container) {
        terminal.info("Server '" + container.getName() + "' has been shut down");
        terminal.exitServer(container);
        terminal.openServer(null);
        container.setInstance(null);
    }

    /**
     * Shutdowns the application.
     */
    public void shutdown() {
        terminal.info("Shutting down...");
        for (final MachineContainer container : containers) {
            if (container.getInstance() == null) continue;
            terminal.info("Shutting down '" + container.getName() + "' server");
            try {
                container.getInstance().shutdown();
            } catch (Exception exception) {
                handleException(exception);
            }
        }
        terminal.info("Machine has been shut down");
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
