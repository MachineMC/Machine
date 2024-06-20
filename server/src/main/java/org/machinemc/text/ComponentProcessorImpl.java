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
package org.machinemc.text;

import com.google.common.base.Preconditions;
import org.machinemc.scriptive.components.*;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.PropertiesSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementation of {@link ComponentProcessor}.
 */
public class ComponentProcessorImpl implements ComponentProcessor {

    private final ComponentSerializer serializer;
    private final Map<Class<? extends Component>, ComponentTransformer<Component>> transformers = new HashMap<>();

    public ComponentProcessorImpl(final ComponentSerializer serializer) {
        this.serializer = Preconditions.checkNotNull(serializer, "Component serializer can not be null");

        // registers transformers for client components
        transformers.put(KeybindComponent.class, component -> (ClientComponent) component);
        transformers.put(TextComponent.class, component -> (ClientComponent) component);
        transformers.put(TranslationComponent.class, component -> (ClientComponent) component);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Component> void registerComponentType(final Class<C> type,
                                                            final Supplier<C> emptySupplier,
                                                            final ComponentTransformer<C> transformer) {
        if (ClientComponent.class.isAssignableFrom(type)) return;
        Preconditions.checkNotNull(type, "Component type can not be null");
        Preconditions.checkNotNull(emptySupplier, "Empty supplier can not be null");
        Preconditions.checkNotNull(transformer, "Transformer can not be null");
        Preconditions.checkState(!transformers.containsKey(type), "Component type '" + type.getName() + "' is already registered");
        serializer.register(type, emptySupplier);
        transformers.put(type, (ComponentTransformer<Component>) transformer);
    }

    @Override
    public ClientComponent transform(final Component component) {
        return ComponentUtils.transformComponentRecursively(transformers::get, component);
    }

    @Override
    public ComponentSerializer getSerializer() {
        return new ComponentSerializer() {

            @Override
            public <C extends Component> void register(final Class<C> type, final Supplier<C> emptySupplier) {
                // during initialization the client component types are automatically registered
                if (!ClientComponent.class.isAssignableFrom(type)) throw new UnsupportedOperationException();
            }

            @Override
            public <T> T serialize(final Component component, final PropertiesSerializer<T> propertiesSerializer) {
                return serializer.serialize(component, propertiesSerializer);
            }

            @Override
            public ComponentProperties serialize(final Component component) {
                return serializer.serialize(component);
            }

            @Override
            public <T> Component deserialize(final T value, final PropertiesSerializer<T> propertiesSerializer) {
                return serializer.deserialize(value, propertiesSerializer);
            }

            @Override
            public Component deserialize(final ComponentProperties properties) {
                return serializer.deserialize(properties);
            }

        };
    }

}
