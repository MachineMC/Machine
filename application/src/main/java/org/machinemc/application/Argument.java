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

import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * All supported java arguments of Machine application.
 * <p>
 * Dash '-' can be used instead of the underscore character
 * when specifying the arguments.
 * Specified arguments can be both upper and lowercase.
 */
public enum Argument {

    /**
     * Enables interactive terminal for the application.
     */
    SMART_TERMINAL,

    /**
     * Disables colors for the console.
     */
    NO_COLORS;

    static @Unmodifiable Set<Argument> parse(final String[] args) {
        final Set<Argument> parsed = new LinkedHashSet<>();
        for (final String argument : args) {
            try {
                parsed.add(Argument.valueOf(argument.toUpperCase().replace("-", "_")));
            } catch (Exception ignored) {

            }
        }
        return Collections.unmodifiableSet(parsed);
    }

}
