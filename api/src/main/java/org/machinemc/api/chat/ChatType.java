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
package org.machinemc.api.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;
import org.machinemc.scriptive.style.ChatStyle;
import org.machinemc.scriptive.style.TextFormat;

import java.util.*;

public interface ChatType extends NBTSerializable {

    /**
     * @return namespaced key of the chat type
     */
    NamespacedKey getName();

    /**
     * @return chat element of the chat type
     */
    Element getChatElement();

    /**
     * @return narration element of the chat type
     */
    Element getNarrationElement();

    /**
     * Chat and Narration types of chat types, contain information
     * about their parameters, translation key and chat format.
     * @param type type of the element
     * @param parameters parameters of the element
     * @param translationKey translation key of the element
     * @param format format of the element
     * @param font font of the element
     */
    record Element(ElementType type,
                   Collection<Parameter> parameters,
                   String translationKey,
                   @Nullable TextFormat format,
                   @Nullable NamespacedKey font) implements NBTSerializable {

        public Element {
            Objects.requireNonNull(type, "Element type can not be null");
            Objects.requireNonNull(parameters, "Parameters can not be null");
            Objects.requireNonNull(translationKey, "Translation key can not be null");
        }

        /**
         * Creates new element of type chat.
         * @param parameters parameters of the element
         * @param translationKey translation key of the element
         * @param format chat format of the element
         * @param font font of the element
         * @return created chat type element
         */
        public static Element chat(final Collection<Parameter> parameters,
                                                final String translationKey,
                                                final @Nullable TextFormat format,
                                                final @Nullable NamespacedKey font) {
            return new Element(ElementType.CHAT, parameters, translationKey, format, font);
        }
        /**
         * Creates new element of type narration.
         * @param parameters parameters of the element
         * @param translationKey translation key of the element
         * @param format chat format of the element
         * @param font font of the element
         * @return created chat type element
         */
        public static Element narration(final Collection<Parameter> parameters,
                                                     final String translationKey,
                                                     final @Nullable TextFormat format,
                                                     final @Nullable NamespacedKey font) {
            return new Element(ElementType.NARRATION, parameters, translationKey, format, font);
        }

        @Override
        public NBTCompound toNBT() {
            final NBTList parameters = new NBTList(this.parameters.stream().map(Parameter::getName).toList());
            final Map<String, String> styleMap = new HashMap<>();
            if (format != null) {
                final Map<ChatStyle, Boolean> styles = format.getStyles();
                for (final Map.Entry<ChatStyle, Boolean> entry : styles.entrySet()) {
                    if (entry.getValue() != null)
                        styleMap.put(entry.getKey().name().toLowerCase(Locale.ENGLISH), entry.getValue().toString());
                }
                format.getColor().ifPresent(color -> styleMap.put("color", color.getName()));
                if (font != null)
                    styleMap.put("font", font.toString());
            }
            final NBTCompound style = new NBTCompound();
            for (final String key : styleMap.keySet())
                style.set(key, styleMap.get(key));
            return new NBTCompound(Map.of(
                    "translation_key", translationKey,
                    "parameters", parameters,
                    "style", style
            ));
        }

    }

    /**
     * Type of chat type element.
     */
    @AllArgsConstructor
    enum ElementType {
        CHAT("chat"),
        NARRATION("narration");

        @Getter
        private final String name;
    }

    /**
     * Parameters used by chat type elements.
     */
    @AllArgsConstructor
    enum Parameter {
        SENDER("sender"),
        TARGET("target"),
        CONTENT("content");

        @Getter
        private final String name;
    }

}
