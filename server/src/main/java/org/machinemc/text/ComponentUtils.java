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
import org.jetbrains.annotations.Contract;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.locale.LocaleLanguage;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Utilities for manipulation with components.
 */
public final class ComponentUtils {

    private ComponentUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Applies locale language instance to a component and
     * all its siblings.
     *
     * @param locale locale language
     * @param component component
     * @return component with new locale language
     */
    @Contract(pure = true)
    public static Component withLocaleLanguageRecursively(final LocaleLanguage locale, final Component component) {
        Component withLocale = component.clone();
        if (withLocale instanceof TranslationComponent translation) {
            final List<Component> arguments = Arrays.stream(translation.getArguments())
                    .map(c -> withLocaleLanguageRecursively(locale, c))
                    .toList();
            translation.setArguments(arguments.toArray(new Component[0]));
            withLocale = translation.withLocaleLanguage(locale);
        }
        final List<Component> siblings = withLocale.getSiblings();
        withLocale.clearSiblings();
        for (final Component sibling : siblings)
            withLocale.append(withLocaleLanguageRecursively(locale, sibling));
        return withLocale;
    }

    /**
     * Recursively transforms a component to client component.
     *
     * @param transformerGetter component type -> transformer function for getting component transformers
     *                          during the transformation process
     * @param component component to transform
     * @return transformed component that can be displayed by client
     */
    @Contract(pure = true)
    public static ClientComponent transformComponentRecursively(final Function<Class<? extends Component>, ComponentTransformer<Component>> transformerGetter, final Component component) {
        Component transformed = component.clone();
        final List<Component> siblings = transformed.getSiblings();
        transformed.clearSiblings();
        final ComponentTransformer<Component> transformer = transformerGetter.apply(transformed.getType());
        Preconditions.checkNotNull(transformer, "Failed to find transformer for type '" + transformed.getType().getName() + "'");
        transformed = transformer.transform(transformed);
        for (final Component sibling : siblings)
            transformed.append(transformComponentRecursively(transformerGetter, sibling));
        return (ClientComponent) transformed;
    }

}
