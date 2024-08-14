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
 * Represents a data-driven registry.
 * <p>
 * Data-driven registries are fully created be the server and sent to the client
 * and can be managed during server's runtime.
 *
 * @param <T> registry entry type
 */
public non-sealed interface DataDrivenRegistry<T> extends Registry<T> {

    /**
     * Returns key of this registry.
     *
     * @return key of this registry
     */
    @Override
    RegistryKey<T, DataDrivenRegistry<T>> key();

    /**
     * Returns writable copy of this registry.
     * <p>
     * Once the provided writable registry is frozen, the changes will
     * be applied to this instance, and in case this is a valid server
     * registry, then also for all currently connected players.
     *
     * @return creates writable copy of this registry
     */
    Writable<T> modify();

    /**
     * Writable data driven registry.
     *
     * @param <T> registry entry type
     */
    interface Writable<T> extends DataDrivenRegistry<T>, Registry.Writable<T> {

        /**
         * Pushes the changes to the data driven registry this writable registry
         * was created from.
         *
         * @return original data driven registry
         */
        DataDrivenRegistry<T> freeze();

    }

}
