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

import com.google.errorprone.annotations.ThreadSafe;
import org.machinemc.scriptive.locale.LocaleLanguage;

import java.util.*;
import java.util.function.Function;

/**
 * Translator of {@link org.machinemc.scriptive.components.TranslationComponent}.
 * <p>
 * It works as a big key <-> value registry for different locales and is mainly
 * used for logging Minecraft messages in the console in readable format.
 */
@ThreadSafe
public interface Translator {

    /**
     * Parses a {@link Locale} from a string in format {@code language_country_variant}.
     *
     * @param locale locale as string
     * @return a locale
     */
    static Optional<Locale> parseLocale(String locale) {
        final String[] segments = locale.split("_", 3);
        return switch (segments.length) {
            case 1 -> Optional.of(Locale.of(segments[0]));
            case 2 -> Optional.of(Locale.of(segments[0], segments[1]));
            case 3 -> Optional.of(Locale.of(segments[0], segments[1], segments[2]));
            default -> Optional.empty();
        };
    }

    /**
     * Returns default locale of this translator.
     *
     * @return locale
     */
    Locale defaultLocale();

    /**
     * Sets the default locale used by this registry.
     *
     * @param locale the locale to use a default
     */
    void defaultLocale(Locale locale);

    /**
     * Registers a translation.
     *
     * @param key a translation key
     * @param locale a locale
     * @param format a translation format
     */
    void register(Locale locale, String key, String format);

    /**
     * Registers a map of translations.
     *
     * @param locale a locale
     * @param formats a map of translation keys to formats
     */
    default void registerAll(Locale locale, Map<String, String> formats) {
        this.registerAll(locale, formats.keySet(), formats::get);
    }

    /**
     * Registers a resource bundle of translations.
     *
     * @param locale a locale
     * @param bundle a resource bundle
     */
    default void registerAll(Locale locale, ResourceBundle bundle) {
        this.registerAll(locale, bundle.keySet(), bundle::getString);
    }

    /**
     * Registers a resource bundle of translations.
     *
     * @param locale locale
     * @param keys the translation keys to register
     * @param function a function to transform a key into a message format
     */
    default void registerAll(Locale locale, Set<String> keys, Function<String, String> function) {
        keys.forEach(key -> register(locale, key, function.apply(key)));
    }

    /**
     * Unregisters a translation key.
     *
     * @param locale locale
     * @param key a translation key
     */
    void unregister(Locale locale, String key);

    /**
     * Returns instance of {@link LocaleLanguage} for given locale, that
     * can be used to translate translation components.
     *
     * @param locale locale
     * @return locale language
     */
    LocaleLanguage getLocaleLanguage(Locale locale);

    /**
     * Returns instance of {@link LocaleLanguage} for default locale of the translator,
     * that can be used to translate translation components.
     *
     * @return locale language
     */
    default LocaleLanguage getLocaleLanguage() {
        return getLocaleLanguage(defaultLocale());
    }

}
