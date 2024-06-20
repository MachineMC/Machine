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
        }
        return localeLanguage;
    }

}
