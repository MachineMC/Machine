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
package org.machinemc.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark a method as an event handler.
 * Methods annotated with @EventHandler will be invoked when the corresponding event is fired.
 * <p>
 * An event handler method must follow this contract:
 * <ul>
 *     <li>Must not be private.</li>
 *     <li>Must not be static.</li>
 *     <li>Must not be abstract.</li>
 *     <li>Must have exactly one parameter of an {@link Event} type.</li>
 * </ul>
 * <p>
 * An example of how an event handler method should look like:
 * <pre>
 *     &#064;EventHandler
 *     public void onPlayerJoin(PlayerJoinEvent event) {
 *         // ...
 *     }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * Defines the priority of the event handler.
     * Handlers with higher priority values have the last say in the event.
     * So, in turn, <b>lower priority values are called before higher priority values</b>.
     * <p>
     * Defaults to {@link EventPriority#NORMAL}.
     *
     * @return the priority of the event handler.
     */
    int priority() default EventPriority.NORMAL;

    /**
     * Indicates whether the event handler should ignore cancelled events.
     * If true, the handler will not be invoked for events that have been cancelled.
     * <p>
     * Defaults to false.
     * 
     * @return true if the handler should ignore cancelled events, false otherwise.
     */
    boolean ignoreCancelled() default false;

    /**
     * Indicates whether the event handler should ignore subclasses of the event type.
     * If true, the handler will not be invoked for events that are subclasses of the specified event type.
     *
     * @return true if the handler should ignore subclasses, false otherwise.
     */
    boolean ignoreSubclasses() default false;

}
