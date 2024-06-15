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
package org.machinemc.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.components.Component;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Logger that is capable of logging components to the console with correct formatting.
 */
public interface ComponentLogger extends Logger {

    /**
     * Log a message at the TRACE level.
     *
     * @param msg the message string to be logged
     */
    void trace(final Component msg);

    /**
     * Log a message at the TRACE level according to the specified format
     * and argument.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the TRACE level. </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    void trace(final Component format, final @Nullable Object arg);

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the TRACE level. </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void trace(final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the TRACE level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for TRACE. The variants taking {@link #trace(Component, Object) one} and
     * {@link #trace(Component, Object, Object) two} arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void trace(final Component format, final @Nullable Object @NotNull ... arguments);

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void trace(final Component msg, final @Nullable Throwable t);

    /**
     * Log a message with the specific Marker at the TRACE level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message string to be logged
     */
    void trace(final Marker marker, final Component msg);

    /**
     * This method is similar to {@link #trace(Component, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    void trace(final Marker marker, final Component format, final @Nullable Object arg);

    /**
     * This method is similar to {@link #trace(Component, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void trace(final Marker marker, final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * This method is similar to {@link #trace(Component, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param argArray an array of arguments
     */
    void trace(final Marker marker, final Component format, final @Nullable Object @NotNull... argArray);

    /**
     * This method is similar to {@link #trace(Component, Throwable)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void trace(final Marker marker, final Component msg, final @Nullable Throwable t);

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    void debug(final Component msg);

    /**
     * Log a message at the DEBUG level according to the specified format
     * and argument.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level. </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    void debug(final Component format, final @Nullable Object arg);

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level. </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void debug(final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the DEBUG level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for DEBUG. The variants taking
     * {@link #debug(Component, Object) one} and {@link #debug(Component, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void debug(final Component format, final @Nullable Object @NotNull... arguments);

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void debug(final Component msg, final @Nullable Throwable t);

    /**
     * Log a message with the specific Marker at the DEBUG level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message string to be logged
     */
    void debug(final Marker marker, final Component msg);

    /**
     * This method is similar to {@link #debug(Component, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    void debug(final Marker marker, final Component format, final @Nullable Object arg);

    /**
     * This method is similar to {@link #debug(Component, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void debug(final Marker marker, final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * This method is similar to {@link #debug(Component, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void debug(final Marker marker, final Component format, final @Nullable Object @NotNull... arguments);

    /**
     * This method is similar to {@link #debug(Component, Throwable)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void debug(final Marker marker, final Component msg, final @Nullable Throwable t);

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    void info(final Component msg);

    /**
     * Log a message at the INFO level according to the specified format
     * and argument.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the INFO level. </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    void info(final Component format, final @Nullable Object arg);

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the INFO level. </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void info(final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the INFO level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for INFO. The variants taking
     * {@link #info(Component, Object) one} and {@link #info(Component, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void info(final Component format, final @Nullable Object@NotNull... arguments);

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void info(final Component msg, final @Nullable Throwable t);

    /**
     * Log a message with the specific Marker at the INFO level.
     *
     * @param marker The marker specific to this log statement
     * @param msg the message string to be logged
     */
    void info(final Marker marker, final Component msg);

    /**
     * This method is similar to {@link #info(Component, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    void info(final Marker marker, final Component format, final @Nullable Object arg);

    /**
     * This method is similar to {@link #info(Component, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void info(final Marker marker, final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * This method is similar to {@link #info(Component, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void info(final Marker marker, final Component format, final @Nullable Object@NotNull... arguments);

    /**
     * This method is similar to {@link #info(Component, Throwable)} method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void info(final Marker marker, final Component msg, final Throwable t);

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    void warn(final Component msg);

    /**
     * Log a message at the WARN level according to the specified format
     * and argument.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the WARN level. </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    void warn(final Component format, final @Nullable Object arg);

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the WARN level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for WARN. The variants taking
     * {@link #warn(Component, Object) one} and {@link #warn(Component, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void warn(final Component format, final @Nullable Object@NotNull... arguments);

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the WARN level. </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void warn(final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void warn(final Component msg, final Throwable t);

    /**
     * Log a message with the specific final Marker at the WARN level.
     *
     * @param marker The marker specific to this log statement
     * @param msg the message string to be logged
     */
    void warn(final Marker marker, final Component msg);

    /**
     * This method is similar to {@link #warn(Component, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    void warn(final Marker marker, final Component format, final @Nullable Object arg);

    /**
     * This method is similar to {@link #warn(Component, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void warn(final Marker marker, final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * This method is similar to {@link #warn(Component, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void warn(final Marker marker, final Component format, final @Nullable Object@NotNull... arguments);

    /**
     * This method is similar to {@link #warn(Component, Throwable)} method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void warn(final Marker marker, final Component msg, final Throwable t);

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    void error(final Component msg);

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.</p>
     *
     * @param format the format string
     * @param arg the argument
     */
    void error(final Component format, final @Nullable Object arg);

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.</p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void error(final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the ERROR level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for ERROR. The variants taking
     * {@link #error(Component, Object) one} and {@link #error(Component, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void error(final Component format, final @Nullable Object@NotNull... arguments);

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void error(final Component msg, final Throwable t);

    /**
     * Log a message with the specific final Marker at the ERROR level.
     *
     * @param marker The marker specific to this log statement
     * @param msg the message string to be logged
     */
    void error(final Marker marker, final Component msg);

    /**
     * This method is similar to {@link #error(Component, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    void error(final Marker marker, final Component format, final @Nullable Object arg);

    /**
     * This method is similar to {@link #error(Component, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    void error(final Marker marker, final Component format, final @Nullable Object arg1, final @Nullable Object arg2);

    /**
     * This method is similar to {@link #error(Component, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    void error(final Marker marker, final Component format, final @Nullable Object@NotNull... arguments);

    /**
     * This method is similar to {@link #error(Component, Throwable)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    void error(final Marker marker, final Component msg, final Throwable t);

}
