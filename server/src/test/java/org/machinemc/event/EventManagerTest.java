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
package org.machinemc.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.machinemc.plugin.Plugin;
import org.machinemc.plugin.PluginMetadata;

import static org.junit.jupiter.api.Assertions.*;

public class EventManagerTest {

    private static final EventManagerImpl EVENT_MANAGER = new EventManagerImpl();

    private final Plugin plugin = new Plugin() {

        @Override
        public void load() {

        }

        @Override
        public void enable() {

        }

        @Override
        public void disable() {

        }

        @Override
        public PluginMetadata getMetadata() {
            return null;
        }

    };

    private TestEvent event;

    @BeforeEach
    final void setUp() {
        event = new TestEvent();
    }

    @AfterEach
    final void cleanup() {
        EVENT_MANAGER.listeners.clear();
    }

    @Test
    void registerListenerWithValidListenerRegistersSuccessfully() {
        EVENT_MANAGER.registerListener(plugin, new TestListener());

        assertEquals(1, EVENT_MANAGER.listeners.size());
    }

    @Test
    void registerListenerWithNullPluginThrowsException() {
        assertThrows(NullPointerException.class, () -> EVENT_MANAGER.registerListener(null, new TestListener()));
    }

    @Test
    void registerListenerWithNullListenerThrowsException() {
        assertThrows(NullPointerException.class, () -> EVENT_MANAGER.registerListener(plugin, null));
    }

    @Test
    void unregisterListenerWithValidListenerUnregistersSuccessfully() {
        final TestListener listener = new TestListener();

        EVENT_MANAGER.registerListener(plugin, listener);
        EVENT_MANAGER.unregisterListener(plugin, listener);

        assertTrue(EVENT_MANAGER.listeners.isEmpty());
    }

    @Test
    void unregisterListenersWithValidPluginUnregistersAllListeners() {
        final TestListener listener1 = new TestListener();
        final TestListener listener2 = new TestListener();
        final OtherTestListener listener3 = new OtherTestListener();

        EVENT_MANAGER.registerListener(plugin, listener1);
        EVENT_MANAGER.registerListener(plugin, listener2);
        EVENT_MANAGER.registerListener(plugin, listener3);
        EVENT_MANAGER.unregisterListeners(plugin, TestListener.class);

        assertEquals(1, EVENT_MANAGER.listeners.size());
    }

    @Test
    void unregisterListenersWithValidPluginUnregistersAllPluginListeners() {
        final TestListener listener1 = new TestListener();
        final TestListener listener2 = new TestListener();

        EVENT_MANAGER.registerListener(plugin, listener1);
        EVENT_MANAGER.registerListener(plugin, listener2);
        EVENT_MANAGER.unregisterListeners(plugin);

        assertTrue(EVENT_MANAGER.listeners.isEmpty());
    }

    @Test
    void fireEventWithRegisteredListenerInvokesListener() {
        final TestListener validListener = new TestListener();

        EVENT_MANAGER.registerListener(plugin, validListener);
        EVENT_MANAGER.fireEvent(event);

        assertEquals(1, event.calls());
    }

    @Test
    void fireEventWithNoRegisteredListenerDoesNotInvokeListener() {
        EVENT_MANAGER.fireEvent(event);
        assertEquals(0, event.calls());
    }

    @Test
    void fireEventWithCancelledEventIgnoresListener() {
        final Object validListener = new Object() {
            @EventHandler(ignoreCancelled = true)
            public void onEvent(final TestEvent event) {
                event.call();
            }
        };

        EVENT_MANAGER.registerListener(plugin, validListener);
        event.cancelled(true);
        EVENT_MANAGER.fireEvent(event);

        assertEquals(0, event.calls());
    }

    private static final class TestListener {

        @EventHandler
        public void onEvent(final TestEvent event) {
            event.call();
        }

    }

    private static final class OtherTestListener {

            @EventHandler
            public void onEvent(final TestEvent event) {
                event.call();
            }

    }

    private static final class TestEvent implements Event, Cancellable {

        private int calls;
        private boolean cancelled;

        public int calls() {
            return calls;
        }

        public void call() {
            calls++;
        }

        @Override
        public boolean cancelled() {
            return cancelled;
        }

        @Override
        public void cancelled(final boolean cancelled) {
            this.cancelled = cancelled;
        }

    }

}
