package org.machinemc.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.machinemc.Server;
import org.machinemc.plugins.Plugin;
import org.machinemc.plugins.PluginMetadata;
import org.slf4j.Logger;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ServerEventManagerTest {

    private static final ServerEventManager eventManager = new ServerEventManager();
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
        public Server getServer() {
            return null;
        }

        @Override
        public File getSource() {
            return null;
        }

        @Override
        public Logger getLogger() {
            return null;
        }

        @Override
        public PluginMetadata getMetadata() {
            return null;
        }

        @Override
        public <Listener> void registerListener(Listener listener) {

        }
    };
    private TestEvent event;

    @BeforeEach
    void setUp() {
        event = new TestEvent();
    }

    @AfterEach
    void cleanup() {
        eventManager.listeners.clear();
    }

    @Test
    void registerListener_withValidListener_registersSuccessfully() {
        eventManager.registerListener(plugin, new TestListener());

        assertEquals(1, eventManager.listeners.size());
    }

    @Test
    void registerListener_withNullPlugin_throwsException() {
        assertThrows(NullPointerException.class, () -> eventManager.registerListener(null, new TestListener()));
    }

    @Test
    void registerListener_withNullListener_throwsException() {
        assertThrows(NullPointerException.class, () -> eventManager.registerListener(plugin, null));
    }

    @Test
    void unregisterListener_withValidListener_unregistersSuccessfully() {
        TestListener listener = new TestListener();

        eventManager.registerListener(plugin, listener);
        eventManager.unregisterListener(plugin, listener);

        assertTrue(eventManager.listeners.isEmpty());
    }

    @Test
    void unregisterListeners_withValidPlugin_unregistersAllListeners() {
        TestListener listener1 = new TestListener();
        TestListener listener2 = new TestListener();
        OtherTestListener listener3 = new OtherTestListener();

        eventManager.registerListener(plugin, listener1);
        eventManager.registerListener(plugin, listener2);
        eventManager.registerListener(plugin, listener3);
        eventManager.unregisterListeners(plugin, TestListener.class);

        assertEquals(1, eventManager.listeners.size());
    }

    @Test
    void unregisterListeners_withValidPlugin_unregistersAllPluginListeners() {
        TestListener listener1 = new TestListener();
        TestListener listener2 = new TestListener();

        eventManager.registerListener(plugin, listener1);
        eventManager.registerListener(plugin, listener2);
        eventManager.unregisterListeners(plugin);

        assertTrue(eventManager.listeners.isEmpty());
    }

    @Test
    void fireEvent_withRegisteredListener_invokesListener() {
        TestListener validListener = new TestListener();

        eventManager.registerListener(plugin, validListener);
        eventManager.fireEvent(event);

        assertEquals(1, event.calls());
    }

    @Test
    void fireEvent_withNoRegisteredListener_doesNotInvokeListener() {
        eventManager.fireEvent(event);
        assertEquals(0, event.calls());
    }

    @Test
    void fireEvent_withCancelledEvent_ignoresListener() {
        Object validListener = new Object() {
            @EventHandler(ignoreCancelled = true)
            public void onEvent(TestEvent event) {
                event.call();
            }
        };

        eventManager.registerListener(plugin, validListener);
        event.cancelled(true);
        eventManager.fireEvent(event);

        assertEquals(0, event.calls());
    }

    private static class TestListener {

        @EventHandler
        public void onEvent(TestEvent event) {
            event.call();
        }

    }

    private static class OtherTestListener {

            @EventHandler
            public void onEvent(TestEvent event) {
                event.call();
            }

    }

    private static class TestEvent implements Event, Cancellable {

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
        public void cancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

    }

}