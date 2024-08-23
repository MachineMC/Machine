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
package org.machinemc.plugins;

import org.machinemc.Server;
import org.slf4j.Logger;

import java.io.File;

/**
 * An interface that must be implemented by all Machine plugins
 */
public interface Plugin {

    /**
     * Loads the plugin. This method is called when the plugin is being loaded.
     */
    void load();

    /**
     * Enables the plugin. This method is called when the plugin is being enabled.
     */
    void enable();

    /**
     * Disables the plugin. This method is called when the plugin is being disabled.
     */
    void disable();

    /**
     * Gets the server instance associated with this plugin.
     *
     * @return the server instance
     */
    Server getServer();

    /**
     * Gets the source file of the plugin.
     *
     * @return the source file
     */
    File getSource();

    /**
     * Gets the logger instance for this plugin.
     *
     * @return the logger instance
     */
    Logger getLogger();

    /**
     * Gets the metadata of the plugin.
     *
     * @return the plugin metadata
     */
    PluginMetadata getMetadata();

    /**
     * Registers a listener for this plugin.
     *
     * @param <Listener> the type of the listener
     * @param listener the listener to be registered
     */
    <Listener> void registerListener(Listener listener);

}
