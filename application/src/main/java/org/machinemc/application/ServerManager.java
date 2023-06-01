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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.server.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Manager of Server containers.
 */
public class ServerManager {

    public static final String SERVERS_FILE_NAME = "servers.json";

    private final MachineApplication application;
    private final File file;

    private final Map<File, ServerContainer> containers = new LinkedHashMap<>();

    public ServerManager(final MachineApplication application) {
        this.application = application;
        file = new File(application.getDirectory(), SERVERS_FILE_NAME);

        if (!file.exists() && !FileUtils.createServerFile(file, SERVERS_FILE_NAME))
            throw new RuntimeException("Failed to create the '" + SERVERS_FILE_NAME + "' file");
    }

    /**
     * Reads all server containers specified in the servers json file.
     */
    public void readFile() throws IOException {

        final JsonParser parser = new JsonParser();
        final FileReader reader = new FileReader(file);
        final JsonObject json;
        try (reader) {
            json = parser.parse(reader).getAsJsonObject();
        }

        for (final Map.Entry<String, JsonElement> serverKey : json.entrySet()) {
            final File dir = new File(application.getDirectory(), serverKey.getKey());

            if (containers.containsKey(dir)) continue;

            application.info("Loading '" + serverKey.getKey() + "' server");
            try {
                if (!dir.exists()) {
                    application.severe("Directory for '" + serverKey.getKey() + "' server does not exist");
                    continue;
                }
                final ServerPlatform platform = application.getPlatform(
                        serverKey.getValue().getAsJsonObject().get("platform").getAsString()
                );
                if (platform == null) {
                    application.severe("Server '" + serverKey.getKey()
                            + "' can not be loaded because it uses non-existing platform");
                    continue;
                }
                containers.put(dir, new ServerContainer(dir, platform));
            } catch (Exception exception) {
                application.handleException(exception);
            }
        }

        reader.close();
    }

    /**
     * Writes new server instances to the servers json.
     */
    public void updateFile() throws IOException {
        final JsonObject json = new JsonObject();
        for (final ServerContainer container : containers.values()) {
            final JsonObject containerJson = new JsonObject();
            containerJson.addProperty("platform", container.getPlatform().getCodeName());
            json.add(container.getDirectory().getName(), containerJson);
        }
        Files.copy(new ByteArrayInputStream(application.getGson().toJson(json).getBytes()),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Loads new server container.
     * @param container container to load
     */
    public void loadContainer(final ServerContainer container) {
        if (containers.containsKey(container.getDirectory())) return;
        containers.put(container.getDirectory(), container);
    }

    /**
     * @return all loaded server containers
     */
    public @Unmodifiable Collection<ServerContainer> getContainers() {
        return Collections.unmodifiableCollection(containers.values());
    }

}
