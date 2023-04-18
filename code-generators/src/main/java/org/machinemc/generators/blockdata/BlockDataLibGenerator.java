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
        final JsonObject json = getSource().getAsJsonObject();
        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getValue().getAsJsonObject().get("properties") == null) continue;
            final JsonObject properties = entry.getValue().getAsJsonObject().get("properties").getAsJsonObject();
            for (final Map.Entry<String, JsonElement> propertyEntry : properties.entrySet()) {
                final Property property = new Property(toCamelCase(propertyEntry.getKey(), true));
                for (final JsonElement propertyValue : propertyEntry.getValue().getAsJsonArray())
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
        for (final Property property : properties.values()) {
            addClass(property.getInterfacePath(), property.generateInterface());
            if (property.getType() != Property.Type.OTHER) continue;
            addClass(property.getPath(), property.generate());
        }
        System.out.println("Loading and generating individual block classes...");
        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            final BlockData blockData = BlockData.create(this, entry.getKey(), entry.getValue().getAsJsonObject());
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
        final String[] words = text.split("[\\W_]+");
        final StringBuilder builder = new StringBuilder();
        boolean capitalize = capitalizeFirst;
        for (final String word : words) {
            final String formattedWord;
            if (capitalize)
                formattedWord = word.isEmpty()
                        ? word
                        : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            else {
                formattedWord = word.toLowerCase();
                capitalize = true;
            }
            builder.append(formattedWord);
        }
        return builder.toString();
    }

}
