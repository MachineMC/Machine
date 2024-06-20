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

import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.serialization.ComponentSerializer;

import java.util.function.Supplier;

/**
 * Service that takes care of {@link org.machinemc.scriptive.components.Component} processing.
 * <p>
 * A Component is an object that represents how text
 * is displayed Minecraft clients. It is used in many specific contexts expecting formatted text,
 * including chat messages, written books, death messages, window titles, and the like.
 */
public interface ComponentProcessor {

    /**
     * Registers new server-side component type to this processor.
     *
     * @param type type of the component
     * @param emptySupplier supplier of empty component of given type
     * @param transformer transformer for the component type
     * @param <C> component type
     */
    <C extends Component> void registerComponentType(Class<C> type, Supplier<C> emptySupplier, ComponentTransformer<C> transformer);

    /**
     * Transforms server-side component to component that
     * can be processed and displayed by client.
     * <p>
     * For the transformation to be successful, component types of
     * the provided component and its siblings have to be registered
     * in this component processor.
     * <p>
     * This transformation is recursive and will also transform the siblings
     * of the component.
     *
     * @param component component
     * @return client component
     */
    ClientComponent transform(Component component);

    /**
     * Returns component serializer of this processor that does
     * not support new component type registration.
     * <p>
     * For component registration use {@link #registerComponentType(Class, Supplier, ComponentTransformer)}.
     *
     * @return component serializer of this processor
     */
    ComponentSerializer getSerializer();

}
