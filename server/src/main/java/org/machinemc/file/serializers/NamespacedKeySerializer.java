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
package org.machinemc.file.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.util.error.ErrorContainer;

/**
 * Cogwheel serializer for namespaced keys.
 */
public class NamespacedKeySerializer implements Serializer<NamespacedKey> {

    @Override
    public void serialize(final NamespacedKey namespacedKey, final DataVisitor dataVisitor) {
        dataVisitor.writeString(namespacedKey.toString());
    }

    @Override
    public @Nullable NamespacedKey deserialize(final DataVisitor dataVisitor, final ErrorContainer errorContainer) {
        try {
            return dataVisitor.readString()
                    .map(NamespacedKey::parse)
                    .orElse(null);
        } catch (IllegalArgumentException exception) {
            errorContainer.error(exception.getMessage());
            return null;
        }
    }

}
