package me.pesekjak.machine.codegen.blockdata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import me.pesekjak.machine.codegen.CodeGenerator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockDataLibGenerator extends CodeGenerator {

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Property> properties = new LinkedHashMap<>();

    public BlockDataLibGenerator(File outputDir) throws IOException {
        super(outputDir, "BlockData", "blocks.json");
    }

    @Override
    public void generate() throws IOException {
        System.out.println("Generating the " + super.getLibraryName() + " library");
        JsonObject json = getSource().getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if(entry.getValue().getAsJsonObject().get("properties") == null) continue;
            JsonObject properties = entry.getValue().getAsJsonObject().get("properties").getAsJsonObject();
            for(Map.Entry<String, JsonElement> propertyEntry : properties.entrySet()) {
                Property property = new Property(toCamelCase(propertyEntry.getKey(), true));
                for(JsonElement propertyValue : propertyEntry.getValue().getAsJsonArray())
                    property.addValue(propertyValue.getAsString());
                if(this.properties.get(property.getName()) != null) {
                    this.properties.get(property.getName()).merge(property);
                    continue;
                }
                this.properties.put(property.getName(), property);
            }
        }
        System.out.println("Loaded " + properties.keySet().size() + " blockdata properties");
        System.out.println("Generating the property and property interface classes...");
        for(Property property : properties.values()) {
            addClass(property.getInterfacePath(), property.generateInterface());
            if(property.getType() != Property.Type.OTHER) continue;
            addClass(property.getPath(), property.generate());
        }
        System.out.println("Loading and generating individual block classes...");
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            BlockData blockData = BlockData.create(this, entry.getKey(), entry.getValue().getAsJsonObject());
            if(blockData == null) continue;
            addClass(blockData.getPath(), blockData.generate());
        }
        super.generate();
    }

    public static String toCamelCase(String text, boolean capitalizeFirst) {
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if(capitalizeFirst)
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            else {
                word = word.toLowerCase();
                capitalizeFirst = true;
            }
            builder.append(word);
        }
        return builder.toString();
    }

}
