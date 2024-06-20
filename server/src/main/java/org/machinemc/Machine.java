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

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.jline.utils.InfoCmp;
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
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.terminal.ServerTerminal;
import org.machinemc.text.ComponentProcessor;
import org.machinemc.text.ComponentProcessorImpl;
import org.machinemc.text.Translator;
import org.machinemc.text.TranslatorImpl;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Locale;

/**
 * Main class of the Machine server and application entry point.
 */
@Getter
public final class Machine implements Server {

    public static final String SERVER_BRAND = "Machine";
    public static final String SERVER_IMPLEMENTATION_VERSION = "1.21";
    public static final int SERVER_IMPLEMENTATION_PROTOCOL = 767;

    private final ServerTerminal terminal = ServerTerminal.get();
    private final Logger logger = ServerTerminal.logger();

    private static final String DEFAULT_LOCALE = "en_us";

    private final Gson gson = new Gson();

    private final ComponentProcessor componentProcessor;
    private final Translator translator;

    private NettyServer nettyServer;

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
        componentProcessor = new ComponentProcessorImpl(new ComponentSerializer());
        translator = new TranslatorImpl(Locale.ENGLISH);
    }

    /**
     * Runs the server.
     */
    public void run() throws Exception {
        Preconditions.checkState(terminal.getLineReader() == null, "There is a different server bound to the terminal");
        // TODO set line reader for terminal here
        //  messages do not get printed to terminal in
        //  application run task without LineReader

        terminal.getTerminal().puts(InfoCmp.Capability.clear_screen);
        logger.info("Loading Machine Server for Minecraft {} (protocol {})", SERVER_IMPLEMENTATION_VERSION, SERVER_IMPLEMENTATION_PROTOCOL);

        loadTranslations(DEFAULT_LOCALE); // TODO server properties
        logger.info("Loaded server language files");

        loadNettyServer();
        logger.info("Loaded server connection");
        nettyServer.bind().get();
    }

    /**
     * Loads translations from {@code data/lang} to
     * server's translator.
     *
     * @param localeName name of the locale
     */
    private void loadTranslations(final String localeName) throws IOException {
        final Locale locale = Translator.parseLocale(localeName).orElseThrow();
        translator.defaultLocale(locale);
        final InputStream is = getClass().getResourceAsStream("/data/lang/" + localeName + ".json");
        if (is == null) return;
        try (is) {
            final JsonObject json = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            json.asMap().forEach((key, format) -> translator.register(locale, key, format.getAsString()));
        }
    }

    /**
     * Creates the instance of netty server.
     */
    private void loadNettyServer() {
        Preconditions.checkState(nettyServer == null, "Netty server has already been loaded");

        // serializer provider
        final SerializerProvider provider = new SerializerProviderImpl();

        // replaces integer with var integer
        provider.addSerializers(DefaultSerializers.class);
        provider.removeSerializer(Serializers.Integer.class);
        provider.addSerializer(VarIntSerializer.class);

        // custom serializers
        provider.addSerializer(new ServerStatusSerializer(gson, componentProcessor.getSerializer()));

        // serialization rules
        provider.addSerializationRules(DefaultSerializationRules.class);

        // packet factory
        final PacketFactory factory = new PacketFactoryImpl(PacketEncoder.varInt(), provider);
        factory.addPackets(PacketGroups.Handshaking.ServerBound.class);
        factory.addPackets(PacketGroups.Status.ClientBound.class);
        factory.addPackets(PacketGroups.Status.ServerBound.class);
        factory.addPackets(PingPackets.class);

        // TODO server properties
        nettyServer = new NettyServer(factory, new InetSocketAddress("localhost", 25565));
    }

}
