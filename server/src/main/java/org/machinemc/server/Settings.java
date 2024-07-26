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
package org.machinemc.server;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents various server settings that can be
 * changed on application start with program arguments.
 */
public enum Settings {

    DEBUG;

    private static Set<Settings> loaded;

    /**
     * Initializes the settings using array
     * of program arguments.
     *
     * @param args program arguments
     */
    public static void initialize(final String[] args) {
        Preconditions.checkState(loaded == null, "Settings have already been initialized");
        loaded = new HashSet<>();
        for (final String arg : args) {
            final Settings setting = Arrays.stream(values()).filter(s -> s.name().equalsIgnoreCase(arg)).findAny().orElse(null);
            if (setting == null) continue;
            loaded.add(setting);
        }
    }

    /**
     * Returns whether this setting has been enabled.
     *
     * @return whether this setting has been enabled
     */
    public boolean isEnabled() {
        Preconditions.checkState(loaded != null, "Settings have not been initialized");
        return loaded.contains(this);
    }

}
