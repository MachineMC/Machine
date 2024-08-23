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
package org.machinemc.event;

import org.machinemc.plugins.Plugin;

/**
 * An interface providing methods to manage event listeners and fire events.
 */
public interface EventManager {

    /**
     * Registers a listener for a specific plugin.
     *
     * @param <Listener> the type of the listener
     * @param plugin the plugin to which the listener is associated
     * @param listener the listener to be registered
     */
    <Listener> void registerListener(Plugin plugin, Listener listener);

    /**
     * Unregisters a listener for a specific plugin.
     *
     * @param <Listener> the type of the listener
     * @param plugin the plugin to which the listener is associated
     * @param listener the listener to be unregistered
     */
    <Listener> void unregisterListener(Plugin plugin, Listener listener);

    /**
     * Unregisters all listeners of a specific type for a specific plugin.
     *
     * @param <Listener> the type of the listener
     * @param plugin the plugin to which the listeners are associated
     * @param listener the class of the listeners to be unregistered
     */
    <Listener> void unregisterListeners(Plugin plugin, Class<Listener> listener);

    /**
     * Unregisters all listeners for a specific plugin.
     *
     * @param plugin the plugin to which the listeners are associated
     */
    void unregisterListeners(Plugin plugin);

    /**
     * Fires an event, notifying all registered listeners.
     *
     * @param event the event to be fired
     */
    void fireEvent(Event event);

}
