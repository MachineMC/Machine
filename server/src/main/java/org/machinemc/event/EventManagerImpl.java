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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.VisibleForTesting;
import org.machinemc.cogwheel.util.ArrayUtils;
import org.machinemc.cogwheel.util.JavaUtils;
import org.machinemc.plugin.Plugin;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of an event manager.
 */
public class EventManagerImpl implements EventManager {

    private static final String GENERIC_LISTENER = Type.getInternalName(GenericListener.class);
    private static final String DELEGATING_LISTENER_FIELD_NAME = "instance";

    private final Map<Class<?>, Set<BridgeInfo>> bridges = new HashMap<>();

    @VisibleForTesting
    final Map<Class<? extends Event>, List<Bridge>> listeners = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <Listener> void registerListener(final Plugin plugin, final Listener listener) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(listener, "Listener cannot be null");

        final Class<?> listenerClass = listener.getClass();

        if (bridges.containsKey(listenerClass)) {
            bridges.get(listenerClass).forEach(bridge -> registerBridge(plugin, listener, bridge));
            return;
        }

        final Set<BridgeInfo> bridges = new HashSet<>();
        boolean hasEventHandler = false;

        for (final Method method : listenerClass.getDeclaredMethods()) {
            if (!validateEventHandlerMethod(method)) continue;

            final Class<?> bridgeClass = defineClassPrivatelyIn(
                    listenerClass,
                    generateBridgeClass(method)
            );

            final BridgeInfo bridgeInfo = new BridgeInfo(
                    method.getName(),
                    (Class<? extends GenericListener>) bridgeClass,
                    (Class<? extends Event>) method.getParameters()[0].getType(),
                    method.getAnnotation(EventHandler.class)
            );

            registerBridge(plugin, listener, bridgeInfo);
            bridges.add(bridgeInfo);
            hasEventHandler = true;
        }

        Preconditions.checkState(hasEventHandler, "Listener " + listenerClass + " does not have any event handlers");

        this.bridges.put(listenerClass, bridges);
    }

    /**
     * Registers a new listener to this event manager.
     *
     * @param plugin listener source
     * @param listener listener instance
     * @param bridge bridge info for the handler
     * @param <Listener> listener
     */
    private <Listener> void registerBridge(final Plugin plugin, final Listener listener, final BridgeInfo bridge) {
        final GenericListener bridgeInstance = JavaUtils.newInstance(bridge.bridgeClass(), ArrayUtils.array(listener.getClass()), listener);
        Preconditions.checkNotNull(bridgeInstance, "Failed to create listener instance for method " + bridge.name());

        final Bridge eventListener = new Bridge(plugin, listener, bridgeInstance, bridge);
        final List<Bridge> list = listeners.computeIfAbsent(bridge.event(), k -> new ArrayList<>());

        list.add(eventListener);
        list.sort(null);
    }

    @Override
    public <Listener> void unregisterListener(final Plugin plugin, final Listener listener) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(listener, "Listener cannot be null");

        if (!bridges.containsKey(listener.getClass())) return;

        for (final BridgeInfo bridgeInfo : bridges.get(listener.getClass())) {
            final List<Bridge> list = listeners.get(bridgeInfo.event());
            if (list == null) continue;
            list.removeIf(bridge -> plugin.equals(bridge.plugin()) && listener.equals(bridge.owner()));
            if (list.isEmpty()) listeners.remove(bridgeInfo.event());
        }
    }

    @Override
    public <Listener> void unregisterListeners(final Plugin plugin, final Class<Listener> listener) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(listener, "Listener cannot be null");

        if (!bridges.containsKey(listener)) return;

        for (final BridgeInfo bridgeInfo : bridges.get(listener)) {
            final List<Bridge> list = listeners.get(bridgeInfo.event());
            if (list == null) continue;
            list.removeIf(bridge -> plugin.equals(bridge.plugin()) && listener.equals(bridge.owner().getClass()));
            if (list.isEmpty()) listeners.remove(bridgeInfo.event());
        }
    }

    @Override
    public void unregisterListeners(final Plugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        bridges.forEach((listenerClass, bridges) -> {
            for (final BridgeInfo bridgeInfo : bridges) {
                final List<Bridge> list = listeners.get(bridgeInfo.event());
                if (list == null) continue;
                list.removeIf(bridge -> plugin.equals(bridge.plugin()));
                if (list.isEmpty()) listeners.remove(bridgeInfo.event());
            }
        });
    }

    @Override
    public void fireEvent(final Event event) {
        for (final Map.Entry<Class<? extends Event>, List<Bridge>> entry : listeners.entrySet()) {
            if (!entry.getKey().isAssignableFrom(event.getClass())) continue;
            for (final Bridge bridge : entry.getValue()) {
                if (bridge.info().handler().ignoreCancelled() && event instanceof Cancellable cancellable && cancellable.cancelled())
                    continue;
                if (bridge.info().handler().ignoreSubclasses() && !entry.getKey().equals(event.getClass()))
                    continue;
                bridge.listener().onEvent(event);
            }
        }
    }

    /**
     * Generates generic listener class for a given event handler method.
     *
     * @param method event handler method
     * @return data for generated generic listener
     */
    private static byte[] generateBridgeClass(final Method method) {
        final String bridgeInternalName = getBridgeInternalName(method);
        final Class<?> listenerClass = method.getDeclaringClass();
        final String listenerDescriptor = Type.getDescriptor(listenerClass);
        final Class<?> eventClass = method.getParameters()[0].getType();

        final ClassWriter writer = new ClassWriter(Opcodes.ASM9);
        writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeInternalName, null, Type.getInternalName(Object.class), new String[]{GENERIC_LISTENER});

        writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, DELEGATING_LISTENER_FIELD_NAME, listenerDescriptor, null, null).visitEnd();

        MethodVisitor visitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(listenerClass)), null, null);
        visitor.visitCode();
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), false);
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        visitor.visitFieldInsn(Opcodes.PUTFIELD, bridgeInternalName, DELEGATING_LISTENER_FIELD_NAME, listenerDescriptor);
        visitor.visitInsn(Opcodes.RETURN);
        visitor.visitMaxs(2, 2);
        visitor.visitEnd();

        visitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "onEvent", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Event.class)), null, null);
        visitor.visitCode();
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, bridgeInternalName, DELEGATING_LISTENER_FIELD_NAME, listenerDescriptor);
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        visitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(eventClass));
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(listenerClass), method.getName(), Type.getMethodDescriptor(method), false);
        visitor.visitInsn(Opcodes.RETURN);
        visitor.visitMaxs(2, 2);
        visitor.visitEnd();

        writer.visitEnd();
        return writer.toByteArray();
    }

    /**
     * Creates class name of a generated generic listener class for
     * an event handler method using its hash and class.
     *
     * @param method event handler method
     * @return internal name of the generated generic listener class
     */
    private static String getBridgeInternalName(final Method method) {
        final Class<?> clazz = method.getDeclaringClass();
        final String packageName = clazz.getPackageName().replace('.', '/');
        String className = clazz.getSimpleName();
        if (className.isEmpty()) { // If the class is an anonymous class it doesn't have a simple name
            className = clazz.getName().substring(packageName.length() + 1);
        }
        return packageName + "/" + className + "$Bridge_" + method.hashCode();
    }

    /**
     * Checks whether the provided method is a valid event handler method.
     *
     * @param method method to check
     * @return whether it is valid event handler method
     */
    private static boolean validateEventHandlerMethod(final Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) return false;
        final int modifiers = method.getModifiers();
        Preconditions.checkState(!Modifier.isStatic(modifiers), "Method " + method.getName() + " cannot be static");
        Preconditions.checkState(!Modifier.isPrivate(modifiers), "Method " + method.getName() + " cannot be private");
        Preconditions.checkState(!Modifier.isAbstract(modifiers), "Method " + method.getName() + " cannot be abstract");
        Preconditions.checkState(method.getParameterCount() == 1, "Method " + method.getName() + " must have exactly one parameter");
        Preconditions.checkState(Event.class.isAssignableFrom(method.getParameters()[0].getType()), "Method " + method.getName() + " must have a parameter of type Event");
        return true;
    }

    /**
     * Defines class inside the package of another class.
     *
     * @param clazz class with the package the new class should be defined in
     * @param bytes class data
     * @return new defined class
     */
    private static Class<?> defineClassPrivatelyIn(final Class<?> clazz, final byte[] bytes) {
        final ClassLoader backup = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
        final Class<?> compiled;
        try {
            compiled = MethodHandles
                    .privateLookupIn(clazz, MethodHandles.lookup())
                    .defineClass(bytes);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        } finally {
            Thread.currentThread().setContextClassLoader(backup);
        }
        return compiled;
    }

    /**
     * Data holder for information about an event handler bridge.
     *
     * @param name name of the method
     * @param bridgeClass source class
     * @param event event type
     * @param handler event handler annotation
     */
    private record BridgeInfo(String name,
                              Class<? extends GenericListener> bridgeClass,
                              Class<? extends Event> event,
                              EventHandler handler) {

        public BridgeInfo {
            Preconditions.checkNotNull(name, "Bridge method name can not be null");
            Preconditions.checkNotNull(bridgeClass, "Bridge class can not be null");
            Preconditions.checkNotNull(event, "Bridge event type can not be null");
            Preconditions.checkNotNull(handler, "Bridge event handler can not be null");
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof BridgeInfo that)) return false;
            return bridgeClass.equals(that.bridgeClass);
        }

        @Override
        public int hashCode() {
            return bridgeClass.hashCode();
        }

    }

    /**
     * Bridge between event handler method and generated generic listener.
     *
     * @param plugin source plugin
     * @param owner source class
     * @param listener generated generic listener
     * @param info bridge info
     */
    private record Bridge(Plugin plugin,
                          Object owner,
                          GenericListener listener,
                          BridgeInfo info) implements Comparable<Bridge> {

        public Bridge {
            Preconditions.checkNotNull(plugin, "Source plugin can not be null");
            Preconditions.checkNotNull(owner, "Source class can not be null");
            Preconditions.checkNotNull(listener, "Generic listener can not be null");
            Preconditions.checkNotNull(info, "Bridge info can not be null");
        }

        @Override
        public int compareTo(final Bridge other) {
            return Integer.compare(info().handler().priority(), other.info().handler().priority());
        }

    }

}
