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

import java.io.File;
import java.util.Objects;

/**
 * Represents context from which new server instance is created.
 * @param application application
 * @param directory directory of the server
 * @param name name of the server
 * @param console console of the server
 * @param platform platform of the server
 */
public record ServerContext(ServerApplication application,
                            File directory,
                            String name,
                            PlatformConsole console,
                            ServerPlatform platform) {

    public ServerContext {
        Objects.requireNonNull(application, "Application of the server can not be null");
        Objects.requireNonNull(directory, "Directory of the server can not be null");
        Objects.requireNonNull(name, "Name of the server can not be null");
        Objects.requireNonNull(console, "Console of the server can not be null");
        Objects.requireNonNull(platform, "Platform of the server can not be null");
    }

}
