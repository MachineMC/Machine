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

import org.jetbrains.annotations.Nullable;

/**
 * Represents a server platform.
 */
public interface ServerPlatform {

    /**
     * @return name of the server platform
     */
    String getName();

    /**
     * @return version of the platform
     */
    String getVersion();

    /**
     * @return description of the platform
     */
    @Nullable String getDescription();

    /**
     * Creates new runnable server instance of this platform for given context.
     * @param context context
     * @return new runnable server from given context
     */
    RunnableServer create(ServerContext context) throws Exception;

}
