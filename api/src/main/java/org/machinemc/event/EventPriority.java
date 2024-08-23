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

/**
 * Predefined constants for event handler priorities.
 * The user is not limited to these values, and can use any integer for priority.
 * <p>
 * Event handlers with lower priority values are executed before those with higher values.
 */
public interface EventPriority {

    /**
     * Lowest priority level.
     * Handlers with this priority are executed before {@link EventPriority#LOW}.
     */
    int LOWER = 100;

    /**
     * Low priority level.
     * Handlers with this priority are executed after {@link EventPriority#LOWER} and before {@link EventPriority#NORMAL}.
     */
    int LOW = 200;

    /**
     * Low priority level.
     * Handlers with this priority are executed after {@link EventPriority#LOW} and before {@link EventPriority#HIGH}.
     */
    int NORMAL = 300;

    /**
     * Low priority level.
     * Handlers with this priority are executed after {@link EventPriority#NORMAL} and before {@link EventPriority#HIGHER}.
     */
    int HIGH = 400;

    /**
     * Low priority level.
     * Handlers with this priority are executed after {@link EventPriority#HIGH} and before {@link EventPriority#MONITOR}.
     */
    int HIGHER = 500;

    /**
     * Monitor priority level.
     * Handlers with this priority are executed last.
     */
    int MONITOR = Integer.MAX_VALUE;

}
