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

/**
 * An interface that provides methods to manage plugins within the server.
 */
public interface PluginManager {

    /**
     * Loads a plugin into the server.
     *
     * @param plugin the plugin to be loaded
     */
    void loadPlugin(Plugin plugin);

    /**
     * Unloads a plugin from the server.
     *
     * @param plugin the plugin to be unloaded
     * @return true if the plugin was successfully unloaded, false otherwise
     */
    boolean unloadPlugin(Plugin plugin);

    /**
     * Retrieves the plugin with the specified name.
     *
     * @param name the name of the plugin to retrieve
     * @return the plugin
     */
    Plugin getPlugin(String name);

    /**
     * Checks if a plugin is enabled.
     *
     * @param name the name of the plugin to check
     * @return true if the plugin is enabled, false otherwise
     */
    boolean isPluginEnabled(String name);

    /**
     * Checks if plugins can be loaded.
     *
     * @return true if plugins can be loaded, false otherwise
     */
    boolean canLoadPlugins();

}
