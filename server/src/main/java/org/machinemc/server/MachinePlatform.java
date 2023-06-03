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

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.Material;
import org.machinemc.application.ServerApplication;
import org.machinemc.application.ServerContext;
import org.machinemc.application.RunnableServer;
import org.machinemc.application.ServerPlatform;
import org.machinemc.server.network.packets.PacketFactory;
import org.machinemc.server.utils.ClassUtils;

import java.util.Arrays;

/**
 * Default Machine server platform.
 */
public class MachinePlatform implements ServerPlatform {

    @Override
    public String getCodeName() {
        return "machine";
    }

    @Override
    public String getName() {
        return Machine.SERVER_BRAND;
    }

    @Override
    public String getVersion() {
        return Machine.SERVER_IMPLEMENTATION_VERSION;
    }

    @Override
    public @Nullable String getDescription() {
        return "Default Machine " + getVersion() + " Minecraft server implementation";
    }

    @Override
    public RunnableServer create(final ServerContext context) throws Exception {
        return new Machine(context);
    }

    @Override
    public void load(final ServerApplication application) {
        Arrays.stream(Material.values()).forEach(Material::createBlockData);
        BlockData.finishRegistration();
        application.info("Loaded materials and block data");

        ClassUtils.loadClass(PacketFactory.class);
        application.info("Loaded all packet mappings");
    }

}
