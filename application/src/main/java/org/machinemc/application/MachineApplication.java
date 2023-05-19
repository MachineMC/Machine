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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.machinemc.server.Machine;
import org.machinemc.server.ServerApplication;

import java.io.File;

/**
 * Implementation of server application for Machine server.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MachineApplication implements ServerApplication {

    /**
     * Application entry point.
     * @param args java arguments
     */
    public static void main(final String[] args) throws Exception {
        new MachineApplication().run(args);
    }

    private void run(final String[] args) throws Exception {
        final File directory = new File("machine-server/");
        new Machine(this, directory, args);
    }

}
