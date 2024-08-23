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
package org.machinemc.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to provide metadata for a plugin.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginMetadata {

    /**
     * The name of the plugin.
     *
     * @return the name of the plugin
     */
    String name();

    /**
     * The description of the plugin.
     *
     * @return the description of the plugin, or an empty string if not provided
     */
    String description() default "";

    /**
     * The version of the plugin.
     *
     * @return the version of the plugin, or an empty string if not provided
     */
    String version() default "";

    /**
     * The author of the plugin.
     *
     * @return the author of the plugin, or an empty string if not provided
     */
    String author() default "";

    /**
     * The website of the plugin.
     *
     * @return the website of the plugin, or an empty string if not provided
     */
    String website() default "";

}
