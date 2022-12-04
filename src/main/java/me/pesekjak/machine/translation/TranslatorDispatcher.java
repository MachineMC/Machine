package me.pesekjak.machine.translation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.PacketImpl;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.ClassUtils;

import java.io.IOException;
import java.util.List;

/**
 * Translator dispatcher, calls registered translators from received packets.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslatorDispatcher {

    @Getter
    private final Machine server;

    private final Multimap<Class<? extends PacketIn>, PacketTranslator<? extends PacketIn>> IN_TRANSLATORS = ArrayListMultimap.create();
    private final Multimap<Class<? extends PacketOut>, PacketTranslator<? extends PacketOut>> OUT_TRANSLATORS = ArrayListMultimap.create();

    /**
     * Creates the default dispatcher with all translators from 'translators' package
     * loaded.
     * @param server server to create the dispatcher for
     * @return created dispatcher with all server's translators loaded
     */
    @SuppressWarnings("unchecked")
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
            if(PacketIn.class.isAssignableFrom(translator.packetClass()))
                dispatcher.registerInTranslator((PacketTranslator<? extends PacketIn>) translator);
            else if(PacketOut.class.isAssignableFrom(translator.packetClass()))
                dispatcher.registerOutTranslator((PacketTranslator<? extends PacketOut>) translator);
            else
                throw new RuntimeException(new IllegalStateException());
        }
        return dispatcher;
    }

    /**
     * Registers the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void registerInTranslator(PacketTranslator<? extends PacketIn> translator) {
        IN_TRANSLATORS.put(translator.packetClass(), translator);
    }

    /**
     * Unregisters the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void unregisterInTranslator(PacketTranslator<? extends PacketIn> translator) {
        IN_TRANSLATORS.remove(translator.packetClass(), translator);
    }

    /**
     * Unregisters all the packet translators listening to packet of provided class.
     * @param packetClass packet class of packet translators to unregister
     */
    public void unregisterInTranslator(Class<? extends PacketIn> packetClass) {
        IN_TRANSLATORS.removeAll(packetClass);
    }

    /**
     * Registers the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void registerOutTranslator(PacketTranslator<? extends PacketOut> translator) {
        OUT_TRANSLATORS.put(translator.packetClass(), translator);
    }

    /**
     * Unregisters the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void unregisterOutTranslator(PacketTranslator<? extends PacketOut> translator) {
        OUT_TRANSLATORS.remove(translator.packetClass(), translator);
    }

    /**
     * Unregisters all the packet translators listening to packet of provided class.
     * @param packetClass packet class of packet translators to unregister
     */
    public void unregisterOutTranslator(Class<? extends PacketOut> packetClass) {
        OUT_TRANSLATORS.removeAll(packetClass);
    }

    /**
     * Unregisters all packet translators of this dispatcher.
     */
    public void clear() {
        IN_TRANSLATORS.clear();
        OUT_TRANSLATORS.clear();
    }

    protected boolean play(ClientConnection connection, PacketImpl packet) {
        if(packet instanceof PacketIn)
            return playIn(connection, (PacketIn) packet);
        else if (packet instanceof PacketOut)
            return playOut(connection, (PacketOut) packet);
        return false;
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that sent the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    protected boolean playIn(ClientConnection connection, PacketIn packet) {
        boolean result = true;
        for(PacketTranslator<? extends PacketIn> translator : IN_TRANSLATORS.get(packet.getClass()))
            result = translator.rawTranslate(connection, packet);
        return result;
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that will receive the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    protected boolean playOut(ClientConnection connection, PacketOut packet) {
        boolean result = true;
        for(PacketTranslator<? extends PacketOut> translator : OUT_TRANSLATORS.get(packet.getClass()))
            result = translator.rawTranslate(connection, packet);
        return result;
    }

    protected void playAfter(ClientConnection connection, PacketImpl packet) {
        if(packet instanceof PacketIn)
            playInAfter(connection, (PacketIn) packet);
        else if (packet instanceof PacketOut)
            playOutAfter(connection, (PacketOut) packet);
    }

    /**
     * Plays all the translators after the packet was received by server
     * @param connection connection that sent the packet
     * @param packet packet
     */
    protected void playInAfter(ClientConnection connection, PacketIn packet) {
        for(PacketTranslator<? extends PacketIn> translator : IN_TRANSLATORS.get(packet.getClass()))
            translator.rawTranslateAfter(connection, packet);
    }

    /**
     * Plays all the translators after the packet was sent by server
     * @param connection connection that received the packet
     * @param packet packet
     */
    protected void playOutAfter(ClientConnection connection, PacketOut packet) {
        for(PacketTranslator<? extends PacketOut> translator : OUT_TRANSLATORS.get(packet.getClass()))
            translator.rawTranslateAfter(connection, packet);
    }

}
