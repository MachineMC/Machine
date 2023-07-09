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

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public final class ServerContainer {

    @Getter
    private final File directory;

    @Getter
    private final ServerPlatform platform;

    @Setter
    private @Nullable RunnableServer instance;

    public ServerContainer(final File directory, final ServerPlatform platform) {
        this.platform = Objects.requireNonNull(platform, "Server platform can not be null");

        this.directory = Objects.requireNonNull(directory, "Server platform can not be null");
        if (!directory.exists() && !directory.mkdirs())
            throw new RuntimeException("Directory " + directory.getPath() + " can not be created");
    }

    /**
     * @return whether the container contains running Machine instance
     */
    public boolean isRunning() {
        return instance != null && instance.isRunning();
    }

    /**
     * @return name of the container
     */
    public String getName() {
        return directory.getName();
    }

    /**
     * @return instance of the container
     */
    public Optional<RunnableServer> getInstance() {
        return Optional.ofNullable(instance);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerContainer that)) return false;
        return directory.equals(that.directory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory);
    }

    @Override
    public String toString() {
        return "ServerContainer(" + getName() + ')';
    }

}
