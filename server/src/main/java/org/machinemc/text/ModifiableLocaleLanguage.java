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
import com.google.errorprone.annotations.ThreadSafe;
import lombok.NoArgsConstructor;
import org.machinemc.scriptive.locale.LocaleLanguage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe implementation of {@link org.machinemc.scriptive.locale.LocaleLanguage} that is backed by
 * a map and can introduce new keys over time.
 */
@ThreadSafe
@NoArgsConstructor
public class ModifiableLocaleLanguage extends LocaleLanguage {

    private final Map<String, String> map = new ConcurrentHashMap<>();

    /**
     * Creates new backed locale language with initial values.
     * <p>
     * The entries from provided map are copied to the backing
     * map of the locale language, the map itself is not used
     * as the backing map.
     *
     * @param map map to copy entries from
     */
    public ModifiableLocaleLanguage(final Map<String, String> map) {
        load(map);
    }

    @Override
    public String getOrDefault(final String node, final String defaultValue) {
        final String value = map.get(node);
        if (value == null) return defaultValue;
        return value;
    }

    @Override
    public boolean has(final String node) {
        return map.containsKey(node);
    }

    /**
     * Loads new entries from the provided map to this locale language.
     *
     * @param map map to load the entries from
     */
    public void load(final Map<String, String> map) {
        Preconditions.checkNotNull(map, "Map with language entries can not be null");
        this.map.putAll(map);
    }

    /**
     * Loads new entry to this locale language.
     *
     * @param key key
     * @param value value
     */
    public void load(final String key, final String value) {
        map.put(key, value);
    }

    /**
     * Removes entry from this locale language.
     *
     * @param key key of the entry to remove
     */
    public void unload(final String key) {
        map.remove(key);
    }

}
