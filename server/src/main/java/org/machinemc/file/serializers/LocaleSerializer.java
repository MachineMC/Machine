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
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.util.error.ErrorContainer;
import org.machinemc.text.Translator;

import java.util.Locale;

/**
 * Cogwheel serializer for locale.
 */
public class LocaleSerializer implements Serializer<Locale> {

    @Override
    public void serialize(final Locale locale, final DataVisitor dataVisitor) {
        dataVisitor.writeString(locale.toString());
    }

    @Override
    public @Nullable Locale deserialize(final DataVisitor dataVisitor, final ErrorContainer errorContainer) {
        final String locale = dataVisitor.readString().orElse(null);
        if (locale == null) return null;
        return Translator.parseLocale(locale).orElse(null);
    }

}
