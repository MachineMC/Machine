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
package org.machinemc.network.protocol.serializers;

import lombok.RequiredArgsConstructor;
import org.machinemc.Machine;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.catalogue.DynamicCatalogue;

import java.util.Collection;
import java.util.List;

/**
 * Catalogue for network serializers.
 */
@RequiredArgsConstructor
public final class MachineNetworkSerializers implements DynamicCatalogue.Serializers {

    private final Machine server;

    @Override
    public Collection<Serializer<?>> provideSerializers() {
        return List.of(
                new GameProfileSerializer(),
                new NamespacedKeySerializer(),
                new PlayerSettingsSerializer(),
                new ServerStatusSerializer(server.getGson(), server.getComponentProcessor().getSerializer())
        );
    }

}
