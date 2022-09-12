package me.pesekjak.machine.events.translations;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.ClassUtils;
import mx.kenzie.mirror.Mirror;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslatorDispatcher implements ServerProperty {

    @Getter
    private final Machine server;

    private final Multimap<Class<? extends PacketIn>, PacketTranslator<? extends PacketIn>> IN_TRANSLATORS = ArrayListMultimap.create();
    private final Multimap<Class<? extends PacketOut>, PacketTranslator<? extends PacketOut>> OUT_TRANSLATORS = ArrayListMultimap.create();

    @SuppressWarnings("unchecked")
    public static TranslatorDispatcher createDefault(Machine server) throws ClassNotFoundException, IOException {
        TranslatorDispatcher dispatcher = new TranslatorDispatcher(server);
        List<String> classes = ClassUtils.getClasses(TranslatorDispatcher.class.getPackageName());
        for(String className : classes) {
            Class<?> translatorClass = Class.forName(className);
            if(!translatorClass.getSuperclass().equals(PacketTranslator.class)) continue;
            PacketTranslator<?> translator = null;
            try {
                translator = (PacketTranslator<?>) Mirror.of(translatorClass)
                        .constructor()
                        .invoke();
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

    public void registerInTranslator(PacketTranslator<? extends PacketIn> translator) {
        IN_TRANSLATORS.put(translator.packetClass(), translator);
    }

    public void unregisterInTranslator(PacketTranslator<? extends PacketIn> translator) {
        IN_TRANSLATORS.remove(translator.packetClass(), translator);
    }

    public void unregisterInTranslator(Class<? extends PacketIn> packetClass) {
        IN_TRANSLATORS.removeAll(packetClass);
    }

    public void registerOutTranslator(PacketTranslator<? extends PacketOut> translator) {
        OUT_TRANSLATORS.put(translator.packetClass(), translator);
    }

    public void unregisterOutTranslator(PacketTranslator<? extends PacketOut> translator) {
        OUT_TRANSLATORS.remove(translator.packetClass(), translator);
    }

    public void unregisterOutTranslator(Class<? extends PacketOut> packetClass) {
        OUT_TRANSLATORS.removeAll(packetClass);
    }

    public void clear() {
        IN_TRANSLATORS.clear();
        OUT_TRANSLATORS.clear();
    }

    protected void play(ClientConnection connection, Packet packet) {
        if(packet instanceof PacketIn)
            playIn(connection, (PacketIn) packet);
        else if (packet instanceof PacketOut)
            playOut(connection, (PacketOut) packet);
    }

    protected void playIn(ClientConnection connection, PacketIn packet) {
        for(PacketTranslator<? extends PacketIn> translator : IN_TRANSLATORS.get(packet.getClass()))
            translator.rawTranslate(connection, packet);
    }

    protected void playOut(ClientConnection connection, PacketOut packet) {
        for(PacketTranslator<? extends PacketOut> translator : OUT_TRANSLATORS.get(packet.getClass()))
            translator.rawTranslate(connection, packet);
    }

}
