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
import org.machinemc.file.ServerProperties;
import org.machinemc.server.ServerStatus;
import org.machinemc.text.ComponentProcessor;
import org.machinemc.text.Translator;

/**
 * Represents a Machine server implementation.
 */
public interface Server {

    /**
     * Serializer registry used by the server with registered
     * serializers for main data types used by Machine.
     *
     * @return server serializer registry
     */
    SerializerRegistry getSerializerRegistry();

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
     *
     * @return server status
     */
    ServerStatus getServerStatus();

    /**
     * Returns the component processor of the server.
     *
     * @return server's component processor
     */
    ComponentProcessor getComponentProcessor();

    /**
     * Returns the translator used by the server.
     *
     * @return server's translator
     */
    Translator getTranslator();

}
