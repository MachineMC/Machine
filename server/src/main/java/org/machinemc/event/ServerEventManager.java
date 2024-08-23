package org.machinemc.event;

import com.google.common.base.Preconditions;
import org.machinemc.cogwheel.util.ArrayUtils;
import org.machinemc.cogwheel.util.JavaUtils;
import org.machinemc.plugins.Plugin;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Exchanger;

public class ServerEventManager implements EventManager {

    private static final String GENERIC_LISTENER = "org/machinemc/event/GenericListener";
    private static final String DELEGATING_LISTENER_FIELD_NAME = "instance";

    private final Map<Class<?>, Set<BridgeInfo>> bridges = new HashMap<>();
    // Package-private to use in tests
    final Map<Class<? extends Event>, List<Bridge>> listeners = new HashMap<>();

    @Override
    public <Listener> void registerListener(Plugin plugin, Listener listener) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(listener, "Listener cannot be null");
        Class<?> listenerClass = listener.getClass();
        if (bridges.containsKey(listenerClass)) {
            for (BridgeInfo bridge : bridges.get(listenerClass))
                registerBridge(plugin, listener, bridge);
            return;
        }
        Set<BridgeInfo> bridges = new HashSet<>();
        boolean hasEventHandler = false;
        for (Method method : listenerClass.getDeclaredMethods()) {
            if (!validateEventHandlerMethod(method)) continue;
            byte[] bridgeByteCode = generateBridgeClass(method);
            //noinspection unchecked
            Class<? extends GenericListener> bridgeClass = (Class<? extends GenericListener>) defineClassPrivatelyIn(
                    listenerClass,
                    bridgeByteCode
            );
            //noinspection unchecked
            BridgeInfo bridgeInfo = new BridgeInfo(
                    method.getName(),
                    bridgeClass,
                    (Class<? extends Event>) method.getParameters()[0].getType(),
                    method.getAnnotation(EventHandler.class)
            );
            registerBridge(plugin, listener, bridgeInfo);
            bridges.add(bridgeInfo);
            hasEventHandler = true;
        }
        if (!hasEventHandler)
            throw new IllegalStateException("Listener " + listenerClass + " does not have any event handlers");
        this.bridges.put(listenerClass, bridges);
    }

    private void registerBridge(Plugin plugin, Object listener, BridgeInfo bridge) {
        GenericListener bridgeInstance = JavaUtils.newInstance(bridge.bridgeClass(), ArrayUtils.array(listener.getClass()), listener);
        if (bridgeInstance == null)
            throw new IllegalStateException("Failed to create listener instance for method " + bridge.name());
        Bridge eventListener = new Bridge(plugin, listener, bridgeInstance, bridge);
        List<Bridge> list = listeners.computeIfAbsent(
                bridge.event(),
                k -> new ArrayList<>()
        );
        list.add(eventListener);
        list.sort(null);
    }

    @Override
    public <Listener> void unregisterListener(Plugin plugin, Listener listener) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(listener, "Listener cannot be null");
        for (BridgeInfo bridgeInfo : bridges.getOrDefault(listener.getClass(), Collections.emptySet())) {
            List<Bridge> list = listeners.get(bridgeInfo.event());
            if (list == null) continue;
            list.removeIf(bridge -> plugin.equals(bridge.plugin()) && listener.equals(bridge.owner()));
            if (list.isEmpty())
                listeners.remove(bridgeInfo.event());
        }
    }

    @Override
    public <Listener> void unregisterListeners(Plugin plugin, Class<Listener> listener) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(listener, "Listener cannot be null");
        for (BridgeInfo bridgeInfo : bridges.getOrDefault(listener, Collections.emptySet())) {
            List<Bridge> list = listeners.get(bridgeInfo.event());
            if (list == null) continue;
            list.removeIf(bridge -> plugin.equals(bridge.plugin()) && listener.equals(bridge.owner().getClass()));
            if (list.isEmpty())
                listeners.remove(bridgeInfo.event());
        }
    }

    @Override
    public void unregisterListeners(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        bridges.forEach((listenerClass, bridges) -> {
            for (BridgeInfo bridgeInfo : bridges) {
                List<Bridge> list = listeners.get(bridgeInfo.event());
                if (list == null) continue;
                list.removeIf(bridge -> plugin.equals(bridge.plugin()));
                if (list.isEmpty())
                    listeners.remove(bridgeInfo.event());
            }
        });
    }

    @Override
    public void fireEvent(Event event) {
        for (Map.Entry<Class<? extends Event>, List<Bridge>> entry : listeners.entrySet()) {
            if (!entry.getKey().isAssignableFrom(event.getClass())) continue;
            for (Bridge bridge : entry.getValue()) {
                if (bridge.info().handler().ignoreCancelled() && event instanceof Cancellable cancellable && cancellable.cancelled())
                    continue;
                if (bridge.info().handler().ignoreSubclasses() && !entry.getKey().equals(event.getClass()))
                    continue;
                bridge.listener().onEvent(event);
            }
        }
    }

    private static byte[] generateBridgeClass(Method method) {
        String bridgeInternalName = getBridgeInternalName(method);
        Class<?> listenerClass = method.getDeclaringClass();
        String listenerDescriptor = listenerClass.descriptorString();
        Class<?> eventClass = method.getParameters()[0].getType();
        ClassWriter writer = new ClassWriter(Opcodes.ASM9);
        writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, bridgeInternalName, null, "java/lang/Object", new String[]{GENERIC_LISTENER});

        writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, DELEGATING_LISTENER_FIELD_NAME, listenerDescriptor, null, null).visitEnd();

        MethodVisitor visitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(" + listenerDescriptor + ")V", null, null);
        visitor.visitCode();
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        visitor.visitFieldInsn(Opcodes.PUTFIELD, bridgeInternalName, DELEGATING_LISTENER_FIELD_NAME, listenerDescriptor);
        visitor.visitInsn(Opcodes.RETURN);
        visitor.visitMaxs(2, 2);
        visitor.visitEnd();

        visitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "onEvent", "(Lorg/machinemc/event/Event;)V", null, null);
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

    private static String getBridgeInternalName(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        String packageName = clazz.getPackageName().replace('.', '/');
        String className = clazz.getSimpleName();
        if (className.isEmpty()) { // If the class is an anonymous class it doesn't have a simple name
            className = clazz.getName().substring(packageName.length() + 1);
        }
        return packageName + "/" + className + "$Bridge_" + method.hashCode();
    }

    private static boolean validateEventHandlerMethod(Method method) {
        if (!method.isAnnotationPresent(EventHandler.class))
            return false;
        if (Modifier.isStatic(method.getModifiers()))
            throw new IllegalStateException("Method " + method.getName() + " cannot be static");
        if (Modifier.isPrivate(method.getModifiers()))
            throw new IllegalStateException("Method " + method.getName() + " cannot be private");
        if (Modifier.isAbstract(method.getModifiers()))
            throw new IllegalStateException("Method " + method.getName() + " cannot be abstract");
        if (method.getParameterCount() != 1)
            throw new IllegalStateException("Method " + method.getName() + " must have exactly one parameter");
        if (!Event.class.isAssignableFrom(method.getParameters()[0].getType()))
            throw new IllegalStateException("Method " + method.getName() + " must have a parameter of type Event");
        return true;
    }

    private static Class<?> defineClassPrivatelyIn(Class<?> clazz, byte[] bytes) {
        Exchanger<Class<?>> exchanger = new Exchanger<>();
        Thread.ofVirtual().start(() -> {
            Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
            Class<?> compiled;
            try {
                compiled = MethodHandles
                        .privateLookupIn(clazz, MethodHandles.lookup())
                        .defineClass(bytes);
            } catch (Exception exception) {
                compiled = null;
            }
            try {
                exchanger.exchange(compiled);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });
        try {
            Class<?> compiled = exchanger.exchange(null);
            if (compiled == null) throw new NullPointerException();
            return compiled;
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    private record BridgeInfo(String name, Class<? extends GenericListener> bridgeClass, Class<? extends Event> event, EventHandler handler) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BridgeInfo that = (BridgeInfo) o;

            return bridgeClass.equals(that.bridgeClass);
        }

        @Override
        public int hashCode() {
            return bridgeClass.hashCode();
        }

    }

    private record Bridge(
            Plugin plugin,
            Object owner,
            GenericListener listener,
            BridgeInfo info
    ) implements Comparable<Bridge> {

        @Override
        public int compareTo(Bridge other) {
            return Integer.compare(info().handler().priority(), other.info().handler().priority());
        }

    }

}
