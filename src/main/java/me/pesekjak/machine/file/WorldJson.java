package me.pesekjak.machine.file;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.dimensions.DimensionType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class WorldJson implements ServerFile, ServerProperty {

    public final static String WORLD_FILE_NAME = "world.json";

    @Getter
    private final Machine server;
    @Getter
    private final NamespacedKey worldName;
    @Getter
    private final DimensionType dimension;
    @Getter
    private final long seed;

    public WorldJson(Machine server, File file) throws IOException, ParseException {
        this.server = server;
        final JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        final String unparsedName = (String) json.get("name");
        NamespacedKey name;
        try {
            name = NamespacedKey.parse(unparsedName);
        } catch (Exception ignored) {
            server.getConsole().severe("World '" + file.getParentFile().getName() + "' uses illegal name identifier and can't be registered");
            throw new ParseException(1);
        }

        worldName = name;

        final String unparsedDimensionType = (String) json.get("dimension");
        NamespacedKey dimensionKey;
        try {
            dimensionKey = NamespacedKey.parse(unparsedDimensionType);
        } catch (Exception ignored) {
            server.getConsole().severe("World '" + file.getParentFile().getName() + "' uses illegal dimension identifier and can't be registered");
            throw new ParseException(1);
        }

        dimension = server.getDimensionTypeManager().getDimension(dimensionKey);
        if(dimension == null) {
            getServer().getConsole().severe("World '" + worldName + "' uses non existing dimension");
            throw new IllegalStateException("World '" + worldName + "' uses non existing dimension");
        }

        long seedValue = 1;
        try {
            seedValue = ((Number) json.get("seed")).longValue();
        } catch (Exception exception) {
            getServer().getConsole().severe("World '" + worldName + "' has not valid defined seed, defaulting to '1' instead");
        }
        seed = seedValue;
    }

    @Override
    public String getName() {
        return WORLD_FILE_NAME;
    }

    @Override
    public InputStream getOriginal() {
        return Machine.CLASS_LOADER.getResourceAsStream(WORLD_FILE_NAME);
    }

    public World world() {
        return World.builder()
                .name(worldName)
                .dimensionType(dimension)
                .seed(seed)
                .build();
    }

}
