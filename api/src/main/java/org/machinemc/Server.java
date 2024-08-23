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
package org.machinemc;

import org.machinemc.cogwheel.serialization.SerializerRegistry;
import org.machinemc.event.EventManager;
import org.machinemc.file.ServerProperties;
import org.machinemc.server.ServerStatus;
import org.machinemc.server.Ticker;
import org.machinemc.text.ComponentProcessor;
import org.machinemc.text.Translator;

/**
 * Represents a Machine server implementation.
 */
public interface Server {

    /**
     * Returns the server's Minecraft version, for example {@code 1.21.1}.
     *
     * @return server's Minecraft version
     */
    String getMinecraftVersion();

    /**
     * Returns the version of the protocol used by the server.
     *
     * @return protocol version of the server
     */
    int getProtocolVersion();

    /**
     * Returns server's ticker.
     * <p>
     * The Ticker is a core component of the server responsible for managing time within the game.
     * It ensures that the game world is updated at regular intervals, known as "ticks".
     * This always happens on the main server thread (tick thread).
     *
     * @return server's ticker
     */
    Ticker getTicker();

    /**
     * Returns the event manager of the server.
     * <p>
     * The EventManager is responsible for managing and dispatching events within the server.
     * It allows for the registration of listeners and the firing of events.
     *
     * @return server's event manager
     */
    EventManager getEventManager();

    /**
     * Serializer registry used by the server with registered
     * serializers for main data types used by Machine.
     *
     * @return server serializer registry
     */
    SerializerRegistry getSerializerRegistry();

    /**
     * Returns the component processor of the server.
     *
     * @return server's component processor
     */
    ComponentProcessor getComponentProcessor();

    /**
     * Returns server properties.
     * <p>
     * Server properties can be customised using the
     * {@code server.properties} file and afford very
     * basic configuration of the server.
     *
     * @return server properties
     */
    ServerProperties getServerProperties();

    /**
     * Returns status of the server displayed in the
     * multiplayer server menu.
     * <p>
     * Each time this method is called, new server status
     * instance with current players is provided.
     *
     * @return server status
     */
    ServerStatus getServerStatus();

    /**
     * Returns the translator used by the server.
     *
     * @return server's translator
     */
    Translator getTranslator();

}
