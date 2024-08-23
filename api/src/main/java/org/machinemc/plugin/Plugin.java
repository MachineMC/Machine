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
package org.machinemc.plugin;

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
     * Gets the metadata of the plugin.
     *
     * @return the plugin metadata
     */
    PluginMetadata getMetadata();

}
