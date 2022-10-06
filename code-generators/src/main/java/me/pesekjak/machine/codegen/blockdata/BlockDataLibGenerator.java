package me.pesekjak.machine.codegen.blockdata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.pesekjak.machine.codegen.CodeGenerator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockDataLibGenerator extends CodeGenerator {

    private final Map<String, Property> properties = new LinkedHashMap<>();

    public BlockDataLibGenerator() throws IOException {
        super("BlockData", "blocks.json");
    }

    @Override
    public void generate() throws IOException {
        System.out.println("Generating the " + super.getLibraryName() + " library");
        JsonObject json = getSource().getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if(entry.getValue().getAsJsonObject().get("properties") == null) continue;
            JsonObject properties = entry.getValue().getAsJsonObject().get("properties").getAsJsonObject();
            for(Map.Entry<String, JsonElement> propertyEntry : properties.entrySet()) {
                Property property = new Property(toCamelCase(propertyEntry.getKey().replaceFirst("minecraft:", "")));
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
        System.out.println("Generating the property classes...");
        for(Property property : properties.values()) {
            if(property.getType() != Property.Type.OTHER) continue;
            addClass(property.getPath(), property.generate());
        }
        super.generate();
    }

    private String toCamelCase(String text) {
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            builder.append(word);
        }
        return builder.toString();
    }

}
