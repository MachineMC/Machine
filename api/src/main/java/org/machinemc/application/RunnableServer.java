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

import com.mojang.brigadier.CommandDispatcher;
import org.machinemc.api.commands.CommandExecutor;

import java.io.File;

/**
 * Represents a server that can be run in a Machine application.
 */
public interface RunnableServer {

    /**
     * @return application that created the server instance
     */
    ServerApplication getApplication();

    /**
     * @return whether the server is running
     */
    boolean isRunning();

    /**
     * @return name of the server
     */
    String getName();

    /**
     * @return directory of the server
     */
    File getDirectory();

    /**
     * @return console of the server
     */
    PlatformConsole getConsole();

    /**
     * @return server platform of this server
     */
    ServerPlatform getPlatform();

    /**
     * @return server's command dispatcher
     */
    CommandDispatcher<CommandExecutor> getCommandDispatcher();

    /**
     * Starts up the server.
     */
    void run() throws Exception;

    /**
     * Shuts down the server.
     */
    void shutdown() throws Exception;

}
