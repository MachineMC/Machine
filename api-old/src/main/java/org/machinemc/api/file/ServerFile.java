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
package org.machinemc.api.file;

import java.io.InputStream;
import java.util.Optional;

/**
 * Represents a file of the server.
 */
public interface ServerFile {

    /**
     * @return name of the file
     */
    String getName();

    /**
     * @return stream of the file from the resources if
     * default version exists.
     */
    Optional<InputStream> getOriginal();

}
