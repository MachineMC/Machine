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
package org.machinemc.server.translation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.Machine;
import org.machinemc.server.network.ClientConnection;
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

    private final Multimap<
            Class<? extends Packet>,
            PacketTranslator<? extends Packet>
            > inTranslators = ArrayListMultimap.create();
    private final Multimap<
            Class<? extends Packet>,
            PacketTranslator<? extends Packet>
            > outTranslators = ArrayListMultimap.create();

    /**
     * Creates the default dispatcher with all translators from 'translators' package
     * loaded.
     * @param server server to create the dispatcher for
     * @return created dispatcher with all server's translators loaded
     */
    public static TranslatorDispatcher createDefault(final Machine server) throws ClassNotFoundException, IOException {
        final TranslatorDispatcher dispatcher = new TranslatorDispatcher(server);
        final List<String> classes = ClassUtils.getClasses(TranslatorDispatcher.class.getPackageName());
        for (final String className : classes) {
            final Class<?> translatorClass = Class.forName(className);
            if (!PacketTranslator.class.isAssignableFrom(translatorClass)
                    || translatorClass.equals(PacketTranslator.class))
                continue;
            PacketTranslator<?> translator = null;
            try {
                translator = (PacketTranslator<?>) translatorClass.getConstructor().newInstance();
            } catch (Exception ignored) { }
            if (translator == null) {
                server.getConsole().severe("Failed to construct " + translatorClass.getSimpleName()
                        + " packet translator, it has no default constructor");
                continue;
            }
            final Packet.PacketState state = PacketFactory.getRegisteredState(translator.packetClass()).orElse(null);
            if (state == null) continue;
            if (Packet.PacketState.in().contains(state))
                dispatcher.registerInTranslator(translator);
            else if (Packet.PacketState.out().contains(state))
                dispatcher.registerOutTranslator(translator);
        }
        return dispatcher;
    }

    /**
     * Registers the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void registerInTranslator(final PacketTranslator<? extends Packet> translator) {
        inTranslators.put(translator.packetClass(), translator);
    }

    /**
     * Unregisters the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void unregisterInTranslator(final PacketTranslator<? extends Packet> translator) {
        inTranslators.remove(translator.packetClass(), translator);
    }

    /**
     * Unregisters all the packet translators listening to packet of provided class.
     * @param packetClass packet class of packet translators to unregister
     */
    public void unregisterInTranslator(final Class<? extends Packet> packetClass) {
        inTranslators.removeAll(packetClass);
    }

    /**
     * Registers the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void registerOutTranslator(final PacketTranslator<? extends Packet> translator) {
        outTranslators.put(translator.packetClass(), translator);
    }

    /**
     * Unregisters the new packet translator for the dispatcher.
     * @param translator translator to register
     */
    public void unregisterOutTranslator(final PacketTranslator<? extends Packet> translator) {
        outTranslators.remove(translator.packetClass(), translator);
    }

    /**
     * Unregisters all the packet translators listening to packet of provided class.
     * @param packetClass packet class of packet translators to unregister
     */
    public void unregisterOutTranslator(final Class<? extends Packet> packetClass) {
        outTranslators.removeAll(packetClass);
    }

    /**
     * Unregisters all packet translators of this dispatcher.
     */
    public void clear() {
        inTranslators.clear();
        outTranslators.clear();
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that sent the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    public boolean play(final ClientConnection connection, final Packet packet) {
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
    public boolean playIn(final ClientConnection connection, final Packet packet) {
        boolean result = true;
        for (final PacketTranslator<? extends Packet> translator : inTranslators.get(packet.getClass()))
            result = translator.rawTranslate(connection, packet);
        return result;
    }

    /**
     * Plays all translators for given packet using given connection.
     * @param connection connection that will receive the packet
     * @param packet packet
     * @return true if the packet wasn't cancelled
     */
    public boolean playOut(final ClientConnection connection, final Packet packet) {
        boolean result = true;
        for (final PacketTranslator<? extends Packet> translator : outTranslators.get(packet.getClass()))
            result = translator.rawTranslate(connection, packet);
        return result;
    }

    /**
     * Plays all the translators after the packet was received by server.
     * @param connection connection that sent the packet
     * @param packet packet
     */
    public void playAfter(final ClientConnection connection, final Packet packet) {
        if (Packet.PacketState.in().contains(packet.getPacketState()))
            playInAfter(connection, packet);
        else if (Packet.PacketState.out().contains(packet.getPacketState()))
            playOutAfter(connection, packet);
    }

    /**
     * Plays all the translators after the packet was received by server.
     * @param connection connection that sent the packet
     * @param packet packet
     */
    public void playInAfter(final ClientConnection connection, final Packet packet) {
        for (final PacketTranslator<? extends Packet> translator : inTranslators.get(packet.getClass()))
            translator.rawTranslateAfter(connection, packet);
    }

    /**
     * Plays all the translators after the packet was sent by server.
     * @param connection connection that received the packet
     * @param packet packet
     */
    public void playOutAfter(final ClientConnection connection, final Packet packet) {
        for (final PacketTranslator<? extends Packet> translator : outTranslators.get(packet.getClass()))
            translator.rawTranslateAfter(connection, packet);
    }

}
