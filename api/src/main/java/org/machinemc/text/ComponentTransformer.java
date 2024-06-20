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

/**
 * Transformer of components supported by server to components
 * supported by the client.
 * <p>
 * This is used during component serialization when sending components
 * to the client.
 * <p>
 * Components supported by the client are
 * {@link org.machinemc.scriptive.components.KeybindComponent},
 * {@link org.machinemc.scriptive.components.TextComponent},
 * {@link org.machinemc.scriptive.components.TranslationComponent}.
 *
 * @param <T> component type
 */
@FunctionalInterface
public interface ComponentTransformer<T extends Component> {

    /**
     * Transforms custom component to a component that is supported
     * and can be displayed by the client.
     * <p>
     * The transformer is not expected to transform any siblings,
     * the component provided will always be without siblings.
     *
     * @param component component to transform (without siblings)
     * @return component supported by the client
     */
    ClientComponent transform(T component);

}
