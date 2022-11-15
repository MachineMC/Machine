package me.pesekjak.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.auth.OnlineServer;
import me.pesekjak.machine.chat.Messenger;
import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.commands.ServerCommands;
import me.pesekjak.machine.entities.EntityManager;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.TranslatorDispatcher;
import me.pesekjak.machine.exception.ExceptionHandler;
import me.pesekjak.machine.file.DimensionsJson;
import me.pesekjak.machine.file.PlayerDataContainer;
import me.pesekjak.machine.file.ServerProperties;
import me.pesekjak.machine.file.WorldJson;
import me.pesekjak.machine.logging.ServerConsole;
import me.pesekjak.machine.logging.Console;
import me.pesekjak.machine.network.ServerConnection;
import me.pesekjak.machine.network.packets.PacketFactory;
import me.pesekjak.machine.server.PlayerManager;
import me.pesekjak.machine.server.schedule.Scheduler;
import me.pesekjak.machine.utils.*;
import me.pesekjak.machine.world.*;
import me.pesekjak.machine.world.biomes.BiomeManager;
import me.pesekjak.machine.world.blocks.BlockManager;
import me.pesekjak.machine.world.dimensions.DimensionType;
import me.pesekjak.machine.world.dimensions.DimensionTypeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Machine {

    public static final String SERVER_BRAND = "Machine";
    public static final String SERVER_IMPLEMENTATION_VERSION = "1.19.2";
    public static final int SERVER_IMPLEMENTATION_PROTOCOL = 760;
    public static final int DEFAULT_TPS = 20;

    public static final ClassLoader CLASS_LOADER = Machine.class.getClassLoader();
    public static final String PACKAGE = "me.pesekjak.machine";

    public static final Path DIRECTORY = FileUtils.getMachineJar().getParentFile().toPath();

    @Getter
    public final int TPS;
    @Getter
    public final int serverResponsiveness;

    @Getter
    private boolean running;

    @Getter @Setter
    private Console console;

    @Getter
    protected ExceptionHandler exceptionHandler;

    @Getter @Nullable
    private OnlineServer onlineServer;

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

    public static void main(String[] args) throws Exception {
        if(System.console() == null) return;
        new Machine(args);
    }

    /**
     * Start of new server.
     */
    private Machine(String[] args) throws Exception {

        final Set<String> arguments = Set.of(args);
        final long start = System.currentTimeMillis();

        final boolean colors = !arguments.contains("nocolors");

        // Setting up console
        try {
            console = new ServerConsole(this, colors);
        } catch (Exception e) {
            System.out.println("Failed to load server console");
            e.printStackTrace();
            System.exit(2);
        }
        console.info("Loading Machine Server on Minecraft " + SERVER_IMPLEMENTATION_VERSION);

        exceptionHandler = new ExceptionHandler(this);

        // Setting up server properties
        File propertiesFile = new File(ServerProperties.PROPERTIES_FILE_NAME);
        if(!propertiesFile.exists()) {
            FileUtils.createFromDefault(propertiesFile);
            FileUtils.createFromDefault(new File(ServerProperties.ICON_FILE_NAME));
        }
        try {
            properties = new ServerProperties(this, propertiesFile);
        } catch (IOException exception) {
            exceptionHandler.handle(exception, "Failed to load server properties");
            System.exit(2);
        }
        console.info("Loaded server properties");

        TPS = properties.getTps();

        serverResponsiveness = properties.getServerResponsiveness();

        // Checking if the port in the properties in empty
        if (!NetworkUtils.available(properties.getServerPort())) {
            console.severe("Failed to bind port '" + properties.getServerPort() + "', it's already in use.");
            console.severe("Perhaps another instance of the server is already running?");
            System.exit(2);
        }

        if(properties.isOnline()) {
            onlineServer = new OnlineServer(this);
        } else {
            console.warning("The server will make no attempt to authenticate usernames and encrypt packets. Beware. " +
                    "While this makes the game possible to play without internet access, it also opens up " +
                    "the ability for others to connect with any username they choose.");
        }

        commandDispatcher = new CommandDispatcher<>();
        ServerCommands.register(this, commandDispatcher);

        Arrays.stream(Material.values()).forEach(Material::createBlockData);
        BlockData.finishRegistration();
        blockManager = BlockManager.createDefault(this);
        console.info("Loaded materials and block data");

        // Loading dimensions json file
        dimensionTypeManager = new DimensionTypeManager(this);
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
            dimensionTypeManager.addDimension(DimensionType.createDefault());
        } else {
            for(DimensionType dimension : dimensions)
                dimensionTypeManager.addDimension(dimension);
        }
        console.info("Registered " + dimensionTypeManager.getDimensions().size() + " dimension types");

        messenger = new Messenger(this);

        try {
            playerDataContainer = new PlayerDataContainer(this);
        } catch (Exception exception) {
            exceptionHandler.handle(exception);
            System.exit(2);
        }

        worldManager = new WorldManager(this);
        try {
            for (Path path : Files.walk(DIRECTORY, 2).collect(Collectors.toSet())) {
                if (!path.endsWith(WorldJson.WORLD_FILE_NAME)) continue;
                if (path.getParent().toString().equals(FileUtils.getMachineJar().getParent())) continue;
                if (!path.getParent().getParent().toString().equals(FileUtils.getMachineJar().getParent())) continue;
                try {
                    WorldJson worldJson = new WorldJson(this, path.toFile());
                    if (worldManager.isRegistered(worldJson.getWorldName())) {
                        console.severe("World with name '" + worldJson.getName() + "' is already registered");
                        continue;
                    }
                    World world = worldJson.buildWorld();
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
                File worldJson = new File(WorldJson.WORLD_FILE_NAME);
                FileUtils.createFromDefaultAndLocate(worldJson, ServerWorld.DEFAULT_WORLD_FOLDER + "/");
                World world = ServerWorld.createDefault(this);
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

        for(World world : worldManager.getWorlds())
            world.load();
        console.info("Loaded all server worlds");

        // TODO Implement biomes json
        biomeManager = BiomeManager.createDefault(this);

        entityManager = EntityManager.createDefault(this);

        playerManager = new PlayerManager(this);

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
            connection = new ServerConnection(this);
        } catch (Exception exception) {
            exceptionHandler.handle(exception);
            System.exit(2);
        }

        try {
            scheduler = new Scheduler(4); // TODO add this to properties
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

    public void shutdown() {
        running = false;
        console.stop();
        console.info("Shutting down...");
        console.info("Saving player data...");
        for(Player player : playerManager.getPlayers()) {
            try { player.getConnection().disconnect(Component.translatable("disconnect.closed"));
            } catch (Exception exception) { exceptionHandler.handle(exception); }
        }
        console.info("Saved all player data");
        console.info("Closing the connection...");
        try { connection.close();
        } catch (Exception ignored) { }
        console.info("Connection has been closed");
        console.info("Saving worlds...");
        for(World world : worldManager.getWorlds()) {
            try { world.save();
            } catch (Exception exception) { exceptionHandler.handle(exception); }
        }
        console.info("Server has been stopped");
        System.exit(0);
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
                .replace("\"%MOTD%\"", GsonComponentSerializer.gson().serialize(properties.getMotd()));
    }

    /**
     * Checks if server is in online mode.
     * @return true if server is in online mode
     */
    public boolean isOnline() {
        return onlineServer != null;
    }

    @Override
    public String toString() {
        return "Machine Server " + SERVER_IMPLEMENTATION_VERSION + " (" + SERVER_IMPLEMENTATION_PROTOCOL + ")";
    }

}
