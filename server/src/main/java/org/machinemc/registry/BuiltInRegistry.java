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
package org.machinemc.registry;

/**
 * Represents a built-in server registry.
 * <p>
 * Built-in registries are loaded first, during server startup and are
 * hard-coded on the client. Because of that their contents can not be
 * changed once loaded and should match the client.
 *
 * @param <T> registry entry type
 */
public non-sealed interface BuiltInRegistry<T> extends Registry<T> {
}
