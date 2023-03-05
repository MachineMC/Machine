package org.machinemc.server.translation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketFactory;
import org.machinemc.server.utils.ClassUtils;

import java.io.IOException;
import java.util.List;

/**
 * Translator dispatcher, calls registered translators from received packets.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslatorDispatcher {

    @Getter
    private final Machine server;

    private final Multimap<Class<? extends Packet>, PacketTranslator<? extends Packet>> IN_TRANSLATORS = ArrayListMultimap.create();
    private final Multimap<Class<? extends Packet>, PacketTranslator<? extends Packet>> OUT_TRANSLATORS = ArrayListMultimap.create();

    /**
     * Creates the default dispatcher with all translators from 'translators' package
     * loaded.
     * @param server server to create the dispatcher for
     * @return created dispatcher with all server's translators loaded
     */
    public static TranslatorDispatcher createDefault(Machine server) throws ClassNotFoundException, IOException {
        TranslatorDispatcher dispatcher = new TranslatorDispatcher(server);
        List<String> classes = ClassUtils.getClasses(TranslatorDispatcher.class.getPackageName());
        for(String className : classes) {
            Class<?> translatorClass = Class.forName(className);
            if(translatorClass.getSuperclass() == null || !translatorClass.getSuperclass().equals(PacketTranslator.class)) continue;
            PacketTranslator<?> translator = null;
            try {
                translator = (PacketTranslator<?>) translatorClass.getConstructor().newInstance();
            } catch (Exception ignored) { }
            if(translator == null) {
                server.getConsole().severe("Failed to construct " + translatorClass.getSimpleName() + " packet translator, " +
                        "it has no default constructor");
                continue;
            }
            final Packet.PacketState state = PacketFactory.getRegisteredState(translator.packetClass());
            if(state == null) continue;
            if(Packet.PacketState.in().contains(state))
                dispatcher.registerInTranslator(translator);
            else if(Packet.PacketState.out().contains(state))
                dispatcher.registerOutTranslator(translator);
        }
        return dispatcher;
    }

    /**
     * Registers the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void registerInTranslator(PacketTranslator<? extends Packet> translator) {
        IN_TRANSLATORS.put(translator.packetClass(), translator);
    }

    /**
     * Unregisters the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void unregisterInTranslator(PacketTranslator<? extends Packet> translator) {
        IN_TRANSLATORS.remove(translator.packetClass(), translator);
    }

    /**
     * Unregisters all the packet translators listening to packet of provided class.
     * @param packetClass packet class of packet translators to unregister
     */
    public void unregisterInTranslator(Class<? extends Packet> packetClass) {
        IN_TRANSLATORS.removeAll(packetClass);
    }

    /**
     * Registers the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void registerOutTranslator(PacketTranslator<? extends Packet> translator) {
        OUT_TRANSLATORS.put(translator.packetClass(), translator);
    }

    /**
     * Unregisters the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void unregisterOutTranslator(PacketTranslator<? extends Packet> translator) {
        OUT_TRANSLATORS.remove(translator.packetClass(), translator);
    }

    /**
     * Unregisters all the packet translators listening to packet of provided class.
     * @param packetClass packet class of packet translators to unregister
     */
    public void unregisterOutTranslator(Class<? extends Packet> packetClass) {
        OUT_TRANSLATORS.removeAll(packetClass);
    }

    /**
     * Unregisters all packet translators of this dispatcher.
     */
    public void clear() {
        IN_TRANSLATORS.clear();
        OUT_TRANSLATORS.clear();
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that sent the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    protected boolean play(ClientConnection connection, Packet packet) {
        if (Packet.PacketState.in().contains(packet.getPacketState()))
            return playIn(connection, packet);
        else if (Packet.PacketState.out().contains(packet.getPacketState()))
            return playOut(connection, packet);
        return false;
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that sent the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    protected boolean playIn(ClientConnection connection, Packet packet) {
        boolean result = true;
        for(PacketTranslator<? extends Packet> translator : IN_TRANSLATORS.get(packet.getClass()))
            result = translator.rawTranslate(connection, packet);
        return result;
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that will receive the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    protected boolean playOut(ClientConnection connection, Packet packet) {
        boolean result = true;
        for(PacketTranslator<? extends Packet> translator : OUT_TRANSLATORS.get(packet.getClass()))
            result = translator.rawTranslate(connection, packet);
        return result;
    }

    /**
     * Plays all the translators after the packet was received by server
     * @param connection connection that sent the packet
     * @param packet packet
     */
    protected void playAfter(ClientConnection connection, Packet packet) {
        if(Packet.PacketState.in().contains(packet.getPacketState()))
            playInAfter(connection, packet);
        else if (Packet.PacketState.out().contains(packet.getPacketState()))
            playOutAfter(connection, packet);
    }

    /**
     * Plays all the translators after the packet was received by server
     * @param connection connection that sent the packet
     * @param packet packet
     */
    protected void playInAfter(ClientConnection connection, Packet packet) {
        for(PacketTranslator<? extends Packet> translator : IN_TRANSLATORS.get(packet.getClass()))
            translator.rawTranslateAfter(connection, packet);
    }

    /**
     * Plays all the translators after the packet was sent by server
     * @param connection connection that received the packet
     * @param packet packet
     */
    protected void playOutAfter(ClientConnection connection, Packet packet) {
        for(PacketTranslator<? extends Packet> translator : OUT_TRANSLATORS.get(packet.getClass()))
            translator.rawTranslateAfter(connection, packet);
    }

}
