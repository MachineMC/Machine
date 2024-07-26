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
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import org.jline.utils.InfoCmp;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.cogwheel.ErrorHandler;
import org.machinemc.cogwheel.config.Configuration;
import org.machinemc.cogwheel.json.JSONConfigSerializer;
import org.machinemc.cogwheel.properties.PropertiesConfigSerializer;
import org.machinemc.cogwheel.serialization.SerializerRegistry;
import org.machinemc.cogwheel.yaml.YamlConfigSerializer;
import org.machinemc.file.ServerProperties;
import org.machinemc.file.ServerPropertiesImpl;
import org.machinemc.file.serializers.CogwheelComponentSerializer;
import org.machinemc.file.serializers.LocaleSerializer;
import org.machinemc.file.serializers.NamespacedKeySerializer;
import org.machinemc.file.serializers.PathSerializer;
import org.machinemc.network.NettyServer;
import org.machinemc.network.protocol.clientinformation.ClientInformationPackets;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.ping.PingPackets;
import org.machinemc.network.protocol.pluginmessage.PluginMesagePackets;
import org.machinemc.network.protocol.serializers.MachineNetworkSerializers;
import org.machinemc.paklet.PacketEncoder;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.PacketFactoryImpl;
import org.machinemc.paklet.SerializerProviderImpl;
import org.machinemc.paklet.serialization.SerializerProvider;
import org.machinemc.paklet.serialization.Serializers;
import org.machinemc.paklet.serialization.VarIntSerializer;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializationRules;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializers;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.JSONPropertiesSerializer;
import org.machinemc.server.*;
import org.machinemc.terminal.LoggingThreadGroup;
import org.machinemc.terminal.ServerTerminal;
import org.machinemc.text.ComponentProcessor;
import org.machinemc.text.ComponentProcessorImpl;
import org.machinemc.text.Translator;
import org.machinemc.text.TranslatorImpl;
import org.machinemc.utils.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Main class of the Machine server and application entry point.
 */
@Getter
public final class Machine implements Server {

    public static final String SERVER_BRAND = "Machine";
    public static final String SERVER_IMPLEMENTATION_VERSION = "1.21";
    public static final int SERVER_IMPLEMENTATION_PROTOCOL = 767;

    private boolean running;

    private final ServerTerminal terminal;
    private final Logger logger;

    private final Gson gson;

    private Ticker ticker;

    private final SerializerRegistry serializerRegistry;
    private final JSONConfigSerializer jsonConfigSerializer;
    private final YamlConfigSerializer yamlConfigSerializer;
    private final PropertiesConfigSerializer propertiesConfigSerializer;

    private final ComponentProcessor componentProcessor;

    private ServerProperties serverProperties;

    @Getter(AccessLevel.NONE)
    private ServerStatus serverStatus;

    private Translator translator;

    private NettyServer nettyServer;

    /**
     * Application entry point.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        Settings.initialize(args);
        final Machine server = new Machine();
        Thread.ofPlatform().group(new LoggingThreadGroup(
                Thread.currentThread().getThreadGroup(),
                "MachineServer",
                server.logger
        )).start(() -> {
            try {
                server.run();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    /**
     * Initializes some parts of the Machine available without any
     * special setup or dependencies.
     * <p>
     * Instance of such a server should not be exposed until calling
     * {@link #run()} first.
     */
    private Machine() {
        terminal = ServerTerminal.get();
        logger = ServerTerminal.logger();

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .disableInnerClassSerialization()
                .disableJdkUnsafe()
                .serializeNulls()
                .create();

        serializerRegistry = new SerializerRegistry();
        final ErrorHandler configErrorHandler = (context, errorEntry) -> logger.error(
                "Failed to load entry '{}' due to {} ({})",
                context.node().getName(),
                errorEntry.type(),
                errorEntry.message());
        jsonConfigSerializer = JSONConfigSerializer.builder()
                .registry(serializerRegistry)
                .errorHandler(configErrorHandler)
                .gson(gson)
                .build();
        yamlConfigSerializer = YamlConfigSerializer.builder()
                .registry(serializerRegistry)
                .errorHandler(configErrorHandler)
                .build();
        propertiesConfigSerializer = PropertiesConfigSerializer.builder()
                .registry(serializerRegistry)
                .errorHandler(configErrorHandler)
                .emptyLineBetweenEntries(true)
                .build();

        componentProcessor = new ComponentProcessorImpl(new ComponentSerializer());
    }

    /**
     * Runs the server.
     */
    public void run() throws Exception {
        Preconditions.checkState(!running, "Server is already running");
        Preconditions.checkState(terminal.getLineReader() == null, "There is a different server bound to the terminal");
        running = true;

        terminal.getTerminal().puts(InfoCmp.Capability.clear_screen);
        logger.info("Loading Machine Server for Minecraft {} (protocol {})...", SERVER_IMPLEMENTATION_VERSION, SERVER_IMPLEMENTATION_PROTOCOL);

        loadSerializerRegistry();

        loadServerProperties();

        serverStatus = new ServerStatus(
                new ServerStatus.Version(SERVER_IMPLEMENTATION_VERSION, SERVER_IMPLEMENTATION_PROTOCOL),
                null, // is calculated with getter
                serverProperties.getMOTD(),
                serverProperties.getIcon().orElse(null),
                serverProperties.enforcesSecureChat()
        );

        translator = new TranslatorImpl(serverProperties.getLanguage());
        logger.info("Loaded server language files");

        ticker = new TickerImpl(Thread.ofPlatform().name("tick-thread"), (float) 1 / Tick.TICK_MILLIS * 1000);
        logger.info("Loaded server ticker");

        loadNettyServer();
        nettyServer.bind().get();

        // TODO server started in ... ms message and then set line reader for terminal and accept console commands
    }

    @Override
    public ServerStatus getServerStatus() {
        return serverStatus.withPlayers(null); // TODO calculate players
    }

    /**
     * Loads serializer registry used by server's configs.
     */
    private void loadSerializerRegistry() {
        serializerRegistry.addSerializer(Component.class, new CogwheelComponentSerializer(componentProcessor.getSerializer(), new JSONPropertiesSerializer()));
        serializerRegistry.addSerializer(Locale.class, new LocaleSerializer());
        serializerRegistry.addSerializer(NamespacedKey.class, new NamespacedKeySerializer());
        serializerRegistry.addSerializer(Path.class, new PathSerializer());
        logger.info("Loaded server configuration serializer registry");
    }

    /**
     * Loads server properties.
     */
    private void loadServerProperties() throws IOException {
        final File propertiesFile = new File(ServerPropertiesImpl.PATH);

        final boolean exists = propertiesFile.exists();
        if (!exists && !propertiesFile.createNewFile()) throw new IOException("Failed to create the server properties file");

        if (!exists) {
            serverProperties = new ServerPropertiesImpl();
            propertiesConfigSerializer.save(propertiesFile, (Configuration) serverProperties);
            final File icon = new File(ServerPropertiesImpl.ICON_PATH);
            if (!icon.exists()) FileUtils.createServerFile(icon, "/" + ServerPropertiesImpl.ICON_PATH);
        } else {
            serverProperties = propertiesConfigSerializer.load(propertiesFile, ServerPropertiesImpl.class);
        }

        logger.info("Loaded server properties");
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
        provider.addSerializers(new MachineNetworkSerializers(this));

        // serialization rules
        provider.addSerializationRules(DefaultSerializationRules.class);

        // packet factory
        final PacketFactory factory = new PacketFactoryImpl(PacketEncoder.varInt(), provider);
        factory.addPackets(PacketGroups.Handshaking.ServerBound.class);
        factory.addPackets(PacketGroups.Status.ClientBound.class);
        factory.addPackets(PacketGroups.Status.ServerBound.class);
        factory.addPackets(PacketGroups.Login.ClientBound.class);
        factory.addPackets(PacketGroups.Login.ServerBound.class);
        factory.addPackets(ClientInformationPackets.class);
        factory.addPackets(PingPackets.class);
        factory.addPackets(PluginMesagePackets.class);

        nettyServer = new NettyServer(this, factory, new InetSocketAddress(serverProperties.getServerIP(), serverProperties.getServerPort()));

        logger.info("Loaded server connection");
    }

}
