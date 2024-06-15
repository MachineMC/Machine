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
package org.machinemc;

import com.google.gson.Gson;
import org.machinemc.network.NettyServer;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.ping.PingPackets;
import org.machinemc.network.protocol.serializers.ServerStatusSerializer;
import org.machinemc.paklet.PacketEncoder;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.PacketFactoryImpl;
import org.machinemc.paklet.SerializerProviderImpl;
import org.machinemc.paklet.serialization.SerializerProvider;
import org.machinemc.paklet.serialization.Serializers;
import org.machinemc.paklet.serialization.VarIntSerializer;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializationRules;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializers;
import org.machinemc.scriptive.serialization.JSONComponentSerializer;

import java.net.InetSocketAddress;

/**
 * Main class of the Machine server and application entry point.
 */
public final class Machine {

    /**
     * Application entry point.
     *
     * @param args arguments
     */
    public static void main(final String[] args) throws Exception {
        final Machine server = new Machine();
        server.run();
    }

    private Machine() {
    }

    /**
     * Runs the server.
     */
    public void run() throws Exception {

        final SerializerProvider provider = new SerializerProviderImpl();
        provider.addSerializers(DefaultSerializers.class);
        provider.removeSerializer(Serializers.Integer.class);
        provider.addSerializer(VarIntSerializer.class);
        provider.addSerializer(new ServerStatusSerializer(new Gson(), new JSONComponentSerializer()));

        provider.addSerializationRules(DefaultSerializationRules.class);

        final PacketFactory factory = new PacketFactoryImpl(PacketEncoder.varInt(), provider);
        factory.addPackets(PacketGroups.Handshaking.ServerBound.class);
        factory.addPackets(PacketGroups.Status.ClientBound.class);
        factory.addPackets(PacketGroups.Status.ServerBound.class);
        factory.addPackets(PingPackets.class);

        final NettyServer server = new NettyServer(factory, new InetSocketAddress("localhost", 25565));
        server.bind().get();
    }

}
