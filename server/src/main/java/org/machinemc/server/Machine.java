package org.machinemc.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import org.machinemc.api.auth.OnlineServer;
import org.machinemc.api.world.Material;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.ComponentSerializerImpl;
import org.machinemc.server.auth.OnlineServerImpl;
import org.machinemc.api.chat.Messenger;
import org.machinemc.server.chat.MessengerImpl;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.server.commands.ServerCommands;
import org.machinemc.api.entities.EntityManager;
import org.machinemc.server.entities.EntityManagerImpl;
import org.machinemc.api.entities.Player;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.file.*;
import org.machinemc.server.file.DimensionsJson;
import org.machinemc.server.file.PlayerDataContainerImpl;
import org.machinemc.server.file.ServerPropertiesImpl;
import org.machinemc.server.file.WorldJson;
import org.machinemc.server.inventory.ItemStack;
import org.machinemc.server.logging.FormattedOutputStream;
import org.machinemc.api.network.ServerConnection;
import org.machinemc.api.server.PlayerManager;
import org.machinemc.server.translation.TranslatorDispatcher;
import org.machinemc.server.exception.ExceptionHandlerImpl;
import org.machinemc.server.logging.ServerConsole;
import org.machinemc.api.logging.Console;
import org.machinemc.server.network.ServerConnectionImpl;
import org.machinemc.server.network.packets.PacketFactory;
import org.machinemc.server.server.PlayerManagerImpl;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.server.world.*;
import org.machinemc.api.world.BlockDataImpl;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldManager;
import org.machinemc.api.world.biomes.BiomeManager;
import org.machinemc.server.world.biomes.BiomeManagerImpl;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.server.world.blocks.BlockManagerImpl;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.server.world.dimensions.DimensionTypeImpl;
import org.machinemc.api.world.dimensions.DimensionTypeManager;
import org.machinemc.server.world.dimensions.DimensionTypeManagerImpl;
import org.machinemc.server.world.particles.ParticleFactory;
import org.jetbrains.annotations.Nullable;
import org.machinemc.server.utils.ClassUtils;
import org.machinemc.server.utils.FileUtils;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Machine implements Server {

    public static final String SERVER_BRAND = "Machine";
    public static final String SERVER_IMPLEMENTATION_VERSION = "1.19.2";
    public static final int SERVER_IMPLEMENTATION_PROTOCOL = 760;
    public static final int DEFAULT_TPS = 20;

    public static final ClassLoader CLASS_LOADER = Machine.class.getClassLoader();
    public static final String API_PACKAGE = "org.machinemc.api";
    public static final String SERVER_PACKAGE = "org.machinemc.server";

    public static final Path DIRECTORY = FileUtils.getMachineJar().getParentFile().toPath();

    @Getter
    private boolean running;

    @Getter
    private Console console;

    @Getter
    protected ExceptionHandler exceptionHandler;

    @Getter
    private @Nullable OnlineServer onlineServer;

    @Getter
    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @Getter
    protected ServerProperties properties;

    @Getter
    protected TranslatorDispatcher translatorDispatcher;

    @Getter
    protected Scheduler scheduler;

    @Getter
    protected CommandDispatcher<CommandExecutor> commandDispatcher;

    @Getter
    protected DimensionTypeManager dimensionTypeManager;
    @Getter
    protected Messenger messenger;
    @Getter
    protected ComponentSerializer componentSerializer;
    @Getter
    protected WorldManager worldManager;
    @Getter
    protected BiomeManager biomeManager;
    @Getter
    protected EntityManager entityManager;
    @Getter
    protected PlayerManager playerManager;
    @Getter
    protected BlockManager blockManager;
    @Getter
    private PlayerDataContainer playerDataContainer;

    @Getter
    protected ServerConnection connection;

    @Getter
    protected World defaultWorld;

    static {
        Factories.BUFFER_FACTORY = FriendlyByteBuf::new;
        Factories.ITEM_FACTORY = ItemStack::new;
        Factories.PARTICLE_FACTORY = ParticleFactory::create;
    }

    public static void main(String[] args) throws Exception {
        if(System.console() == null) return;
        new Machine(args);
    }

    private Machine(String[] args) throws Exception {

        final Set<String> arguments = Set.of(args);
        final long start = System.currentTimeMillis();

        final boolean colors = !arguments.contains("nocolors");
        componentSerializer = new ComponentSerializerImpl(); // TODO register other server related component types (NBTComponent, ScoreComponent, SelectorComponent)

        // Setting up console
        try {
            console = new ServerConsole(this, colors);
            System.setOut(new PrintStream(new FormattedOutputStream(((ServerConsole) console), Level.INFO, "[stdout] ")));
            System.setErr(new PrintStream(new FormattedOutputStream(((ServerConsole) console), Level.SEVERE, "[stderr] ")));
        } catch (Exception e) {
            System.out.println("Failed to load server console");
            e.printStackTrace();
            System.exit(2);
        }
        console.info("Loading Machine Server on Minecraft " + SERVER_IMPLEMENTATION_VERSION);
        scheduler = new Scheduler(4);
        exceptionHandler = new ExceptionHandlerImpl(this);

        // Setting up server properties
        File propertiesFile = new File(ServerPropertiesImpl.PROPERTIES_FILE_NAME);
        if(!propertiesFile.exists()) {
            FileUtils.createFromDefault(propertiesFile);
            FileUtils.createFromDefault(new File(ServerPropertiesImpl.ICON_FILE_NAME));
        }
        try {
            properties = new ServerPropertiesImpl(this, propertiesFile);
        } catch (IOException exception) {
            exceptionHandler.handle(exception, "Failed to load server properties");
            System.exit(2);
        }
        console.info("Loaded server properties");

        // Checking if the port in the properties in empty
        if (!NetworkUtils.available(properties.getServerPort())) {
            console.severe("Failed to bind port '" + properties.getServerPort() + "', it's already in use.");
            console.severe("Perhaps another instance of the server is already running?");
            System.exit(2);
        }

        if(properties.isOnline()) {
            onlineServer = new OnlineServerImpl(this);
        } else {
            console.warning("The server will make no attempt to authenticate usernames and encrypt packets. Beware. " +
                    "While this makes the game possible to play without internet access, it also opens up " +
                    "the ability for others to connect with any username they choose.");
        }

        commandDispatcher = new CommandDispatcher<>();
        ServerCommands.register(this, commandDispatcher);

        Arrays.stream(Material.values()).forEach(Material::createBlockData);
        BlockDataImpl.finishRegistration();
        blockManager = BlockManagerImpl.createDefault(this);
        console.info("Loaded materials and block data");

        // Loading dimensions json file
        dimensionTypeManager = new DimensionTypeManagerImpl(this);
        File dimensionsFile = new File(DimensionsJson.DIMENSIONS_FILE_NAME);
        if(!dimensionsFile.exists())
            FileUtils.createFromDefault(dimensionsFile);
        Set<DimensionType> dimensions = new LinkedHashSet<>();
        try {
            dimensions = new DimensionsJson(this, dimensionsFile).dimensions();
        } catch (Exception exception) {
            console.severe("Failed to load the dimensions file");
        }

        // Registering all dimensions from the file into the manager
        if(dimensions.size() == 0) {
            console.warning("There are no defined dimensions in the dimensions file, loading default dimension instead");
            dimensionTypeManager.addDimension(DimensionTypeImpl.createDefault());
        } else {
            for(DimensionType dimension : dimensions)
                dimensionTypeManager.addDimension(dimension);
        }
        console.info("Registered " + dimensionTypeManager.getDimensions().size() + " dimension types");

        messenger = new MessengerImpl(this);

        try {
            playerDataContainer = new PlayerDataContainerImpl(this);
        } catch (Exception exception) {
            exceptionHandler.handle(exception);
            System.exit(2);
        }

        worldManager = new WorldManagerImpl(this);
        try {
            for (Path path : Files.walk(DIRECTORY, 2).collect(Collectors.toSet())) {
                if (!path.endsWith(WorldJson.WORLD_FILE_NAME)) continue;
                if (path.getParent().toString().equals(FileUtils.getMachineJar().getParent())) continue;
                if (!path.getParent().getParent().toString().equals(FileUtils.getMachineJar().getParent())) continue;
                try {
                    final WorldJson worldJson = new WorldJson(this, path.toFile());
                    if (worldManager.isRegistered(worldJson.getWorldName())) {
                        console.severe("World with name '" + worldJson.getName() + "' is already registered");
                        continue;
                    }
                    final World world = worldJson.buildWorld();
                    worldManager.addWorld(world);
                    console.info("Registered world '" + world.getName() + "'");
                } catch (IOException exception) {
                    console.severe("World file '" + path + "' failed to load");
                }
            }
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to load the server worlds from server directory");
        }

        if(worldManager.getWorlds().size() == 0) {
            console.warning("There are no valid worlds in the server folder, default world will be created");
            try {
                final File worldJson = new File(WorldJson.WORLD_FILE_NAME);
                FileUtils.createFromDefaultAndLocate(worldJson, ServerWorld.DEFAULT_WORLD_FOLDER + "/");
                final World world = ServerWorld.createDefault(this);
                worldManager.addWorld(world);
            } catch (Exception exception) {
                exceptionHandler.handle(exception, "Failed to create the default world");
                System.exit(2);
            }
        }
        defaultWorld = worldManager.getWorld(properties.getDefaultWorld());
        if(defaultWorld == null) {
            defaultWorld = worldManager.getWorlds().stream().iterator().next();
            console.warning("Default world in the server properties doesn't exist, using '" + defaultWorld.getName() + "' instead");
        }

        for(World world : worldManager.getWorlds()) {
            try {
                world.load();
            } catch (Exception exception) {
                exceptionHandler.handle(exception, "Failed to load world '" + world.getName() + "'");
            }
        }
        console.info("Loaded all server worlds");

        // TODO Implement biomes json
        biomeManager = BiomeManagerImpl.createDefault(this);

        entityManager = EntityManagerImpl.createDefault(this);

        playerManager = new PlayerManagerImpl(this);

        ClassUtils.loadClass(PacketFactory.class);
        console.info("Loaded all packet mappings");

        try {
            translatorDispatcher = TranslatorDispatcher.createDefault(this);
        } catch (Exception exception) {
            exceptionHandler.handle(exception, "Failed to load packet translator dispatcher");
            System.exit(2);
        }
        console.info("Loaded all packet translators");

        try {
            connection = new ServerConnectionImpl(this);
        } catch (Exception exception) {
            exceptionHandler.handle(exception);
            System.exit(2);
        }

        try {
            console.start();
        } catch (Exception exception) {
            exceptionHandler.handle(exception);
            System.exit(2);
        }

        running = true;
        console.info("Server loaded in " + (System.currentTimeMillis() - start) + "ms");
        scheduler.run(); // blocks the thread

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
    public void shutdown() {
        running = false;
        console.stop();
        console.info("Shutting down...");
        console.info("Saving player data...");
        for(Player player : playerManager.getPlayers()) {
            try {
                player.getConnection().disconnect(TranslationComponent.of("disconnect.closed"));
            } catch (Exception exception) {
                exceptionHandler.handle(exception);
            }
        }
        console.info("Saved all player data");
        console.info("Closing the connection...");
        try {
            connection.close();
        } catch (Exception ignored) { }
        console.info("Connection has been closed");
        console.info("Saving worlds...");
        for(World world : worldManager.getWorlds()) {
            try {
                world.save();
            } catch (Exception exception) {
                exceptionHandler.handle(exception);
            }
        }
        console.info("Server has been stopped");
        System.exit(0);
    }

    @Override
    public String toString() {
        return "Machine Server " + SERVER_IMPLEMENTATION_VERSION + " (" + SERVER_IMPLEMENTATION_PROTOCOL + ")";
    }

    /**
     * Builds the MOTD json of the server in the multiplayer server list.
     * @return MOTD json of the server
     */
    public String statusJson() {
        JsonObject json = new JsonObject();
        JsonObject versionJson = new JsonObject();
        versionJson.addProperty("name", SERVER_IMPLEMENTATION_VERSION);
        versionJson.addProperty("protocol", SERVER_IMPLEMENTATION_PROTOCOL);
        json.add("version", versionJson);
        JsonObject playersJson = new JsonObject();
        playersJson.addProperty("max", properties.getMaxPlayers());
        playersJson.addProperty("online", 0);
        json.add("players", playersJson);
        json.addProperty("description", "%MOTD%");
        if (properties.getIcon() != null)
            json.addProperty("favicon", "data:image/png;base64," + properties.getEncodedIcon());
        return gson
                .toJson(json)
                .replace("\"%MOTD%\"", properties.getMotd().toJson());
    }

}
