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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link Translator}.
 */
public class TranslatorImpl implements Translator {

    private Locale defaultLocale;
    private final Map<Locale, ModifiableLocaleLanguage> localeLanguageMap = new ConcurrentHashMap<>();

    public TranslatorImpl(final Locale defaultLocale) {
        defaultLocale(defaultLocale);
    }

    @Override
    public Locale defaultLocale() {
        return defaultLocale;
    }

    @Override
    public void defaultLocale(final Locale locale) {
        defaultLocale = Preconditions.checkNotNull(locale, "Default Locale can not be null");
        try {
            loadLocaleDefaults(defaultLocale);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void register(final Locale locale, final String key, final String format) {
        getLocaleLanguage(locale).load(key, format);
    }

    @Override
    public void unregister(final Locale locale, final String key) {
        getLocaleLanguage(locale).unload(key);
    }

    @Override
    public ModifiableLocaleLanguage getLocaleLanguage(final Locale locale) {
        ModifiableLocaleLanguage localeLanguage = localeLanguageMap.get(locale);
        if (localeLanguage == null) {
            localeLanguage = new ModifiableLocaleLanguage();
            localeLanguageMap.put(locale, localeLanguage);
            try {
                loadLocaleDefaults(locale);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return localeLanguage;
    }

    /**
     * Loads language file from server resources for given locale to the
     * translator.
     *
     * @param locale locale to load the translations for
     */
    private void loadLocaleDefaults(final Locale locale) throws IOException {
        final InputStream is = getClass().getResourceAsStream("/data/lang/" + locale + ".json");
        if (is == null) return;
        try (is) {
            final JsonObject json = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            json.asMap().forEach((key, format) -> register(locale, key, format.getAsString()));
        }
    }

}
