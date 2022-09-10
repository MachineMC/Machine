package me.pesekjak.machine;

import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.file.DimensionsJson;
import me.pesekjak.machine.file.ServerProperties;
import me.pesekjak.machine.file.WorldJson;
import me.pesekjak.machine.logging.Console;
import me.pesekjak.machine.logging.IConsole;
import me.pesekjak.machine.network.ServerConnection;
import me.pesekjak.machine.network.packets.PacketFactory;
import me.pesekjak.machine.utils.*;
import me.pesekjak.machine.world.PersistentWorld;
import me.pesekjak.machine.world.dimensions.DimensionType;
import me.pesekjak.machine.world.dimensions.DimensionTypeManager;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.WorldManager;
import me.pesekjak.machine.world.biomes.BiomeManager;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Machine {

    public static final String SERVER_BRAND = "Machine";
    public static final String SERVER_IMPLEMENTATION_VERSION = "1.19.2";
    public static final int SERVER_IMPLEMENTATION_PROTOCOL = 760;

    public static final ClassLoader CLASS_LOADER = Machine.class.getClassLoader();
    public static final String PACKAGE = "me.pesekjak.machine";

    public static final Path DIRECTORY = FileUtils.getMachineJar().getParentFile().toPath();

    @Getter(AccessLevel.PROTECTED)
    private final Unsafe UNSAFE;

    @Getter @Setter
    private IConsole console;

    @Getter
    protected ServerProperties properties;
    @Getter
    protected DimensionTypeManager dimensionTypeManager;
    @Getter
    protected WorldManager worldManager;
    @Getter
    protected BiomeManager biomeManager;
    @Getter
    protected ServerConnection connection;

    @Getter
    protected PersistentWorld defaultWorld;

    public static void main(String[] args) throws Exception {
        new Machine();
    }

    public Machine() throws Exception {

        final long start = System.currentTimeMillis();

        // Setting up Unsafe instance and colored terminal
        Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
        unsafeConstructor.setAccessible(true);
        UNSAFE = unsafeConstructor.newInstance();
        unsafeConstructor.setAccessible(false);
        UNSAFE.coloredTerminal();

        // Setting up console
        console = new Console(this);
        console.info("Loading Machine Server on Minecraft " + SERVER_IMPLEMENTATION_VERSION);

        // Setting up server properties
        File propertiesFile = new File(ServerProperties.PROPERTIES_FILE_NAME);
        if(!propertiesFile.exists())
            FileUtils.createFromDefault(propertiesFile);
        properties = new ServerProperties(propertiesFile);
        console.info("Loaded server properties");

        // Checking if the port in the properties in empty
        if (!NetworkUtils.available(properties.getServerPort())) {
            console.severe("Failed to bind port '" + properties.getServerPort() + "', it's already in use.");
            console.severe("Perhaps another instance of the server is already running?");
            System.exit(2);
        }

        // Loading dimensions json file
        File dimensionsFile = new File(DimensionsJson.DIMENSIONS_FILE_NAME);
        if(!dimensionsFile.exists())
            FileUtils.createFromDefault(dimensionsFile);
        Set<DimensionType> dimensions;
        try {
            dimensions = new DimensionsJson(this, dimensionsFile).dimensions();
        } catch (Exception exception) {
            console.severe("Failed to load the dimensions file");
            return;
        }

        // Registering all dimensions from the file into the manager
        dimensionTypeManager = new DimensionTypeManager(this);
        if(dimensions.size() == 0) {
            console.warning("There are no defined dimensions in the dimensions file, loading default dimension instead");
            dimensionTypeManager.addDimension(DimensionType.OVERWORLD);
        } else {
            for(DimensionType dimension : dimensions)
                dimensionTypeManager.addDimension(dimension);
        }
        console.info("Registered " + dimensionTypeManager.getDimensions().size() + " dimension types");

        // Loading all worlds from folders (maybe needs cleanup?)
        final Set<World> worlds = new LinkedHashSet<>();
        final Set<NamespacedKey> registeredWorlds = new HashSet<>();
        for(Path path : Files.walk(DIRECTORY, 2).collect(Collectors.toSet())) {
            if(!path.endsWith(WorldJson.WORLD_FILE_NAME)) continue;
            if(path.getParent().toString().equals(FileUtils.getMachineJar().getParent())) continue;
            if(!path.getParent().getParent().toString().equals(FileUtils.getMachineJar().getParent())) continue;
            try {
                WorldJson worldJson = new WorldJson(this, path.toFile());
                if (registeredWorlds.contains(worldJson.getWorldName())) {
                    console.severe("World with name '" + worldJson.getWorldName() + "' is already registered");
                    continue;
                }
                String levelFolder = path.getParent().toString();
                PersistentWorld world = new PersistentWorld(levelFolder.substring(levelFolder.lastIndexOf("\\") + 1), worldJson.world());
                registeredWorlds.add(world.getName());
                worlds.add(world);
            } catch (IllegalStateException ignored) {
                // Non-existing dimension type, handled in the WorldJson class
            } catch (IOException | ParseException exception) {
                console.severe("World file '" + path + "' failed to load");
            }
        }

        // Registering all worlds from folders into the manager
        if(worlds.size() == 0) {
            console.warning("There are no valid worlds in the server folder, default world will be created");
            File worldJson = new File(WorldJson.WORLD_FILE_NAME);
            FileUtils.createFromDefaultAndLocate(worldJson, PersistentWorld.DEFAULT_WORLD_FOLDER + "/");
            worlds.add(new PersistentWorld(PersistentWorld.DEFAULT_WORLD_FOLDER, World.MAIN));
        }
        worldManager = new WorldManager(this);
        for(World world : worlds) {
            if(worldManager.isRegistered(world.getName())) {
                console.severe("World '" + world.getName() + "' can't be registered, because another world with a same" +
                        "name already exists");
                continue;
            }
            console.info("Loaded world '" + world.getName() + "'");
            worldManager.addWorld(world);
        }
        defaultWorld = (PersistentWorld) worldManager.getWorld(properties.getDefaultWorld());
        if(defaultWorld == null) {
            defaultWorld = (PersistentWorld) worldManager.getWorlds().get(0);
            console.warning("Default world in the server properties doesn't exist, using '" + defaultWorld.getName() + "' instead");
        }

        // TODO Finish Biomes (+ BiomeEffects with Particles) and implement biomes json
        biomeManager = BiomeManager.createDefault(this);

        ClassUtils.loadClass(PacketFactory.class);
        console.info("Loaded all packet mappings");

        connection = new ServerConnection(this);

        console.info("Server loaded in " + (System.currentTimeMillis() - start) + "ms");
    }

    @SuppressWarnings("unchecked")
    public String statusJson() {
        JSONObject json = new JSONObject();

        JSONObject versionJson = new JSONObject();
        versionJson.put("name", SERVER_IMPLEMENTATION_VERSION);
        versionJson.put("protocol", SERVER_IMPLEMENTATION_PROTOCOL);
        json.put("version", versionJson);

        JSONObject playersJson = new JSONObject();
        playersJson.put("max", properties.getMaxPlayers());
        playersJson.put("online", 0);
        json.put("players", playersJson);

        json.put("description", "%MOTD%");

        TreeMap<String, Object> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        treeMap.putAll(json);

        return new GsonBuilder().create()
                .toJson(treeMap)
                .replace("\"%MOTD%\"", GsonComponentSerializer.gson().serialize(properties.getMotd()));
    }

}