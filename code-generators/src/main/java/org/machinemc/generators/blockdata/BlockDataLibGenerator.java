package org.machinemc.generators.blockdata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import org.machinemc.generators.CodeGenerator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockDataLibGenerator extends CodeGenerator {

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Property> properties = new LinkedHashMap<>();

    public BlockDataLibGenerator(final File outputDir) throws IOException {
        super(outputDir, "blockdata", "blocks.json");
    }

    @Override
    public void generate() throws IOException {
        System.out.println("Generating the " + super.getLibraryName() + " library");
        JsonObject json = getSource().getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getValue().getAsJsonObject().get("properties") == null) continue;
            JsonObject properties = entry.getValue().getAsJsonObject().get("properties").getAsJsonObject();
            for (Map.Entry<String, JsonElement> propertyEntry : properties.entrySet()) {
                Property property = new Property(toCamelCase(propertyEntry.getKey(), true));
                for (JsonElement propertyValue : propertyEntry.getValue().getAsJsonArray())
                    property.addValue(propertyValue.getAsString());
                if (this.properties.get(property.getName()) != null) {
                    this.properties.get(property.getName()).merge(property);
                    continue;
                }
                this.properties.put(property.getName(), property);
            }
        }
        System.out.println("Loaded " + properties.keySet().size() + " blockdata properties");
        System.out.println("Generating the property and property interface classes...");
        for (Property property : properties.values()) {
            addClass(property.getInterfacePath(), property.generateInterface());
            if (property.getType() != Property.Type.OTHER) continue;
            addClass(property.getPath(), property.generate());
        }
        System.out.println("Loading and generating individual block classes...");
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            BlockData blockData = BlockData.create(this, entry.getKey(), entry.getValue().getAsJsonObject());
            if (blockData == null) continue;
            addClass(blockData.getPath(), blockData.generate());
        }
        super.generate();
    }

    /**
     * Converts string to camel case.
     * @param text text to convert
     * @param capitalizeFirst whether the first letter should be capitalize
     * @return camel case text
     */
    public static String toCamelCase(final String text, final boolean capitalizeFirst) {
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        boolean capitalize = capitalizeFirst;
        for (String word : words) {
            if (capitalize)
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            else {
                word = word.toLowerCase();
                capitalize = true;
            }
            builder.append(word);
        }
        return builder.toString();
    }

}
