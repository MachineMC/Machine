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
package org.machinemc.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.auth.OnlineServer;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.entities.EntityManager;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.damagetypes.DamageTypeManager;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.file.PlayerDataContainer;
import org.machinemc.api.file.ServerProperties;
import org.machinemc.api.server.PlayerManager;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldManager;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeManager;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.api.world.dimensions.DimensionTypeManager;
import org.machinemc.application.*;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.ComponentSerializerImpl;
import org.machinemc.server.chat.ServerMessenger;
import org.machinemc.server.commands.MachineCommands;
import org.machinemc.server.entities.ServerEntityManager;
import org.machinemc.server.entities.damagetypes.ServerDamageTypeManager;
import org.machinemc.server.exception.ServerExceptionHandler;
import org.machinemc.server.file.*;
import org.machinemc.server.network.NettyServer;
import org.machinemc.server.server.ServerPlayerManager;
import org.machinemc.server.translation.TranslatorDispatcher;
import org.machinemc.server.utils.FileUtils;
import org.machinemc.server.utils.NetworkUtils;
import org.machinemc.server.world.ServerWorld;
import org.machinemc.server.world.ServerWorldManager;
import org.machinemc.server.world.biomes.ServerBiome;
import org.machinemc.server.world.biomes.ServerBiomeManager;
import org.machinemc.server.world.blocks.ServerBlockManager;
import org.machinemc.server.world.dimensions.ServerDimensionType;
import org.machinemc.server.world.dimensions.ServerDimensionTypeManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Machine implements Server, RunnableServer {

    public static final String SERVER_BRAND = "Machine";
    public static final String SERVER_IMPLEMENTATION_VERSION = "1.20.1";
    public static final int SERVER_IMPLEMENTATION_PROTOCOL = 763;
    public static final int DEFAULT_TPS = 20;

    public static final ClassLoader CLASS_LOADER = Machine.class.getClassLoader();
    public static final String API_PACKAGE = "org.machinemc.api";
    public static final String SERVER_PACKAGE = "org.machinemc.server";

    @Getter
    private final ServerApplication application;

    @Getter
    private volatile boolean running;

    @Getter
    private final String name;

    @Getter
    private final File directory;

    @Getter
    private final PlatformConsole console;

    @Getter
    private final ServerPlatform platform;

    @Getter
    private TranslatorDispatcher translatorDispatcher;

    @Getter
    private final ExceptionHandler exceptionHandler;

    private @Nullable OnlineServer onlineServer;

    @Getter
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @Getter
    private ServerProperties properties;

    @Getter
    private final Scheduler scheduler;

    @Getter
    private CommandDispatcher<CommandExecutor> commandDispatcher;

    @Getter
    private DimensionTypeManager dimensionTypeManager;
    @Getter
    DamageTypeManager damageTypeManager;
    @Getter
    private Messenger messenger;
    @Getter
    private ComponentSerializer componentSerializer;
    @Getter
    private WorldManager worldManager;
    @Getter
    private BiomeManager biomeManager;
    @Getter
    private EntityManager entityManager;
    @Getter
    private PlayerManager playerManager;
    @Getter
    private BlockManager blockManager;
    @Getter
    private PlayerDataContainer playerDataContainer;

    @Getter
    private NettyServer connection;

    @Getter
    private World defaultWorld;

    public Machine(final ServerContext context) throws Exception {
        this.application = Objects.requireNonNull(context.application(), "Application of context can not be null");

        if (!context.directory().exists() && !context.directory().mkdirs())
            throw new RuntimeException("Failed to create the server directory");
        this.directory = context.directory();

        this.name = Objects.requireNonNull(context.name(), "Name of the server can not be null");

        this.console = Objects.requireNonNull(context.console(), "Console of the server can not be null");

        this.platform = Objects.requireNonNull(context.platform(), "Platform of the server can not be null");

        scheduler = new Scheduler(4);
        exceptionHandler = new ServerExceptionHandler(this);
    }

    /**
     * Starts the Machine server.
     */
    public void run() throws Exception {
        if (running) throw new RuntimeException("The server is already running");

        final long start = System.currentTimeMillis();

        // TODO register other server related component types (NBTComponent, ScoreComponent, SelectorComponent)
        componentSerializer = new ComponentSerializerImpl();

        console.info("Loading Machine Server on Minecraft " + SERVER_IMPLEMENTATION_VERSION);

        // Setting up server properties
        final File propertiesFile = new File(directory, ServerPropertiesImpl.PROPERTIES_FILE_NAME);
        if (!propertiesFile.exists()) {
            FileUtils.createServerFile(directory, ServerPropertiesImpl.PROPERTIES_FILE_NAME);
            FileUtils.createServerFile(directory, ServerPropertiesImpl.ICON_FILE_NAME);
        }
        try {
            properties = new ServerPropertiesImpl(this, propertiesFile);
        } catch (IOException exception) {
            exceptionHandler.handle(exception, "Failed to load server properties");
            application.stopServer(this);
        }
        console.info("Loaded server properties");

        // Checking if the port in the properties in empty
        if (!NetworkUtils.available(properties.getServerPort())) {
            console.severe("Failed to bind port '" + properties.getServerPort() + "', it's already in use.");
            console.severe("Perhaps another instance of the server is already running?");
            application.stopServer(this);
        }

        if (properties.isOnline()) {
            onlineServer = new OnlineServer(this);
        } else {
            console.warning("The server will make no attempt to authenticate usernames and encrypt packets. Beware. "
                    + "While this makes the game possible to play without internet access, it also opens up "
                    + "the ability for others to connect with any username they choose.");
        }

        commandDispatcher = new CommandDispatcher<>();
        MachineCommands.register(this, commandDispatcher);

        blockManager = ServerBlockManager.createDefault(this);

        // Loading dimensions json file
        dimensionTypeManager = new ServerDimensionTypeManager(this);
        final File dimensionsFile = new File(directory, DimensionsJSON.DIMENSIONS_FILE_NAME);
        if (!dimensionsFile.exists())
            FileUtils.createServerFile(directory, DimensionsJSON.DIMENSIONS_FILE_NAME);
        Set<DimensionType> dimensions = new LinkedHashSet<>();
        try {
            dimensions = new DimensionsJSON(this, dimensionsFile).dimensions();
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to load the dimensions file");
        }

        // Registering all dimensions from the file into the manager
        if (dimensions.isEmpty()) {
            console.warning("There are no defined dimensions in the dimensions file, "
                    + "loading default dimension instead");
            dimensionTypeManager.addDimension(ServerDimensionType.createDefault());
        } else {
            for (final DimensionType dimension : dimensions)
                dimensionTypeManager.addDimension(dimension);
        }
        console.info("Registered " + dimensionTypeManager.getDimensions().size() + " dimension types");

        damageTypeManager = ServerDamageTypeManager.createDefault(this);
        console.info("Registered " + damageTypeManager.getDamageTypes().size() + " damage types");

        messenger = ServerMessenger.createDefault(this);
        console.info("Registered " + messenger.getChatTypes().size() + " chat types");

        // Loading biomes json file
        biomeManager = new ServerBiomeManager(this);
        final File biomesFile = new File(directory, BiomesJSON.BIOMES_FILE_NAME);
        if (!biomesFile.exists())
            FileUtils.createServerFile(directory, BiomesJSON.BIOMES_FILE_NAME);
        Set<Biome> biomes = new LinkedHashSet<>();
        try {
            biomes = new BiomesJSON(this, biomesFile).biomes();
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to load the biomes file");
        }

        // Registering all biomes from the file into the manager
        if (biomes.isEmpty()) {
            console.warning("There are no defined biomes in the biomes file, "
                    + "loading default biome instead");
            biomeManager.addBiome(ServerBiome.createDefault());
        } else {
            for (final Biome biome : biomes)
                biomeManager.addBiome(biome);
        }
        console.info("Registered " + biomeManager.getBiomes().size() + " biomes");

        entityManager = ServerEntityManager.createDefault(this);

        playerManager = new ServerPlayerManager(this);

        try {
            playerDataContainer = new ServerPlayerDataContainer(
                    this,
                    new File(directory, ServerPlayerDataContainer.DEFAULT_PLAYER_DATA_FOLDER)
            );
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to create player data container");
            application.stopServer(this);
        }

        worldManager = new ServerWorldManager(this);
        try (Stream<Path> paths = Files.walk(directory.toPath(), 2)) {
            for (final Path path : paths.collect(Collectors.toSet())) {
                if (!path.endsWith(WorldJSON.WORLD_FILE_NAME)) continue;
                if (path.getNameCount() < 3) continue;
                if (!Files.isSameFile(directory.toPath(), path.getParent().getParent())) continue;
                try {
                    final WorldJSON worldJson = new WorldJSON(this, path.toFile());
                    if (worldManager.isRegistered(worldJson.getWorldName())) {
                        console.severe("World with name '" + worldJson.getName() + "' is already registered");
                        continue;
                    }
                    final World world = worldJson.buildWorld();
                    worldManager.addWorld(world);
                    console.info("Registered world '" + world.getName() + "'");
                } catch (IOException exception) {
                    exceptionHandler.handle(exception);
                }
            }
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to load the server worlds from server directory");
        }

        if (worldManager.getWorlds().isEmpty()) {
            console.warning("There are no valid worlds in the server folder, default world will be created");
            try {
                FileUtils.createServerFile(
                        new File(directory, ServerWorld.DEFAULT_WORLD_FOLDER + "/" + WorldJSON.WORLD_FILE_NAME),
                        WorldJSON.WORLD_FILE_NAME
                );
                final World world = ServerWorld.createDefault(this);
                worldManager.addWorld(world);
            } catch (Exception exception) {
                exceptionHandler.handle(exception, "Failed to create the default world");
                application.stopServer(this);
            }
        }
        defaultWorld = worldManager.getWorld(properties.getDefaultWorld()).orElseGet(() -> {
            final World def = worldManager.getWorlds().stream().iterator().next();
            console.warning("Default world in the server properties doesn't exist, "
                    + "using '" + def.getName() + "' instead");
            return def;
        });

        for (final World world : worldManager.getWorlds()) {
            try {
                world.load();
            } catch (Exception exception) {
                exceptionHandler.handle(exception, "Failed to load world '" + world.getName() + "'");
            }
        }
        console.info("Loaded all server worlds");

        try {
            translatorDispatcher = TranslatorDispatcher.createDefault(this);
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to load packet translator dispatcher");
            application.stopServer(this);
        }
        console.info("Loaded all packet translators");

        Scheduler.task((input, session) -> {
            try {
                connection = new NettyServer(this);
                connection.start();
            } catch (Exception exception) {
                exceptionHandler.handle(exception);
                application.stopServer(this);
            }
            return null;
        }).async().run(scheduler);

        try {
            console.start();
        } catch (Exception exception) {
            exceptionHandler.handle(exception);
            application.stopServer(this);
        }

        running = true;
        console.info("Server loaded in " + (System.currentTimeMillis() - start) + "ms");
        scheduler.run(); // blocks the thread

        if (!running) return;
        shutdown();
    }

    @Override
    public String getBrand() {
        return SERVER_BRAND;
    }

    @Override
    public String getImplementationVersion() {
        return SERVER_IMPLEMENTATION_VERSION;
    }

    @Override
    public int getImplementationProtocol() {
        return SERVER_IMPLEMENTATION_PROTOCOL;
    }

    @Override
    public boolean isOnline() {
        return onlineServer != null;
    }

    @Override
    public Optional<OnlineServer> getOnlineServer() {
        return Optional.ofNullable(onlineServer);
    }

    @Override
    public void shutdown() {
        running = false;
        console.info("Shutting down...");
        console.info("Saving player data...");
        for (final Player player : playerManager.getPlayers()) {
            try {
                player.getConnection().disconnect(TranslationComponent.of("disconnect.closed")).sync();
            } catch (Exception exception) {
                exceptionHandler.handle(exception);
            }
        }
        console.info("Saved all player data");
        console.info("Closing the connection...");
        try {
            connection.close().sync();
        } catch (Exception ignored) { }
        console.info("Connection has been closed");
        console.info("Saving worlds...");
        for (final World world : worldManager.getWorlds()) {
            try {
                world.save();
            } catch (Exception exception) {
                exceptionHandler.handle(exception);
            }
        }
        console.info("Shutting down scheduler");
        try {
            scheduler.shutdown();
        } catch (InterruptedException exception) {
            exceptionHandler.handle(exception);
        }
        console.info("Server has been stopped");
        application.stopServer(this);
    }

    @Override
    public String toString() {
        return "Machine Server " + SERVER_IMPLEMENTATION_VERSION + " (" + SERVER_IMPLEMENTATION_PROTOCOL + ")";
    }

    /**
     * Builds the MOTD json of the server in the multiplayer server list.
     * @return MOTD json of the server
     */
    public String statusJSON() {
        final JsonObject json = new JsonObject();
        final JsonObject versionJson = new JsonObject();
        versionJson.addProperty("name", SERVER_IMPLEMENTATION_VERSION);
        versionJson.addProperty("protocol", SERVER_IMPLEMENTATION_PROTOCOL);
        json.add("version", versionJson);
        final JsonObject playersJson = new JsonObject();
        playersJson.addProperty("max", properties.getMaxPlayers());
        playersJson.addProperty("online", 0);
        json.add("players", playersJson);
        json.addProperty("description", "%MOTD%");
        properties.getEncodedIcon().ifPresent(icon ->
                json.addProperty("favicon", "data:image/png;base64," + icon));
        return gson
                .toJson(json)
                .replace("\"%MOTD%\"", properties.getMOTD().toJson());
    }

}
