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
package org.machinemc.application.terminal;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.logging.Console;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.PlatformConsole;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.style.ChatCode;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.util.ChatUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Base of a terminal.
 */
public abstract class BaseTerminal implements ApplicationTerminal {

    private final MachineApplication application;

    @Setter
    private boolean colors;
    private final InputStream in;
    private final OutputStream out;

    @Getter
    @Setter
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Getter @Setter
    private String
            configPrefix = "CONFIG",
            infoPrefix = "INFO",
            warningPrefix = "WARNING",
            severePrefix = "SEVERE";
    @Setter
    private @Nullable Colour
            configColor = null,
            infoColor = null,
            warningColor = ChatColor.GOLD,
            severeColor = ChatColor.RED;

    protected BaseTerminal(final MachineApplication application,
                           final boolean colors,
                           final InputStream in,
                           final OutputStream out) {
        this.application = application;
        this.colors = colors;
        this.in = in;
        this.out = out;
    }

    @Override
    public MachineApplication getApplication() {
        return application;
    }

    @Override
    public boolean isColored() {
        return colors;
    }

    @Override
    public void sendMessage(final @Nullable UUID sender, final Component message, final MessageType type) {
        sendMessage(null, sender, message, type);
    }

    @Override
    public void sendMessage(final @Nullable PlatformConsole source,
                            final @Nullable UUID sender,
                            final Component message,
                            final MessageType type) {
        if (colors) {
            info(source, ChatUtils.consoleFormatted(message));
        } else {
            info(source, ChatUtils.consoleFormatted(message));
        }
    }

    /**
     * @return config color of the terminal
     */
    public Optional<Colour> getConfigColor() {
        return Optional.ofNullable(configColor);
    }

    /**
     * @return info color of the terminal
     */
    public Optional<Colour> getInfoColor() {
        return Optional.ofNullable(infoColor);
    }

    /**
     * @return warning color of the terminal
     */
    public Optional<Colour> getWarningColor() {
        return Optional.ofNullable(warningColor);
    }

    /**
     * @return severe color of the terminal
     */
    public Optional<Colour> getSevereColor() {
        return Optional.ofNullable(severeColor);
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * Logs the given messages with the specified logging level, using the provided logger.
     *
     * @param logger the logger to use for logging the messages
     * @param source source of the message
     * @param level the logging level to use
     * @param messages the messages to log
     */
    protected void log(final SourcedLogger logger,
                       final @Nullable PlatformConsole source,
                       final Level level,
                       final String... messages) {
        final String prefix = getPrefix(level);
        final String date = now();
        for (final String message : messages) {
            final String messagePrefix = (source != null
                    ? source.getSource().getName() + " | "
                    : "application | ")
                    + "[" + date + "] " + prefix;
            final String formatted = colors
                    ? messagePrefix
                    + ChatUtils.consoleFormatted(message)
                    + ChatColor.RESET.getConsoleFormat()
                    : messagePrefix + stripColorCodes(message);
            logger.log(source, formatted);
        }
    }

    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)§([\\dabcdefklmnor])");

    /**
     * Removes minecraft color codes from the message.
     * @param input input message
     * @return message without the color codes
     */
    public static String stripColorCodes(final String input) {
        return COLOR_CODE_PATTERN.matcher(input).replaceAll(matchResult ->
                ChatCode.byChar(matchResult.group(1)).isEmpty() ? matchResult.group() : ""
        );
    }

    /**
     * Returns the logging prefix for the given logging level.
     *
     * @param level the logging level to get the prefix for
     * @return the logging prefix for the given level
     */
    protected String getPrefix(final Level level) {
        return switch (level.intValue()) {
            case 700 -> (colors && configColor != null
                    ? configColor.getConsoleFormat()
                    : ""
            ) + configPrefix + ": "; // Config value
            case 800 -> (colors && infoColor != null
                    ? infoColor.getConsoleFormat()
                    : ""
            ) + infoPrefix + ": "; // Info value
            case 900 -> (colors && warningColor != null
                    ? warningColor.getConsoleFormat()
                    : ""
            ) + warningPrefix + ": "; // Warning value
            case 1000 -> (colors && severeColor != null
                    ? severeColor.getConsoleFormat()
                    : ""
            ) + severePrefix + ": "; // Severe value
            default -> "";
        };
    }

    /**
     * Returns a current date using console's date formatter.
     * @return formatted date
     */
    protected String now() {
        return dateFormatter != null ? dateFormatter.format(LocalDateTime.now()) : "";
    }


    /**
     * Called when command inside the application is executed when
     * no server instance is active within the terminal.
     * @param input command to execute
     * @return result
     */
    public int executeApplication(final String input) {
        return getApplication().execute(input);
    }

    @FunctionalInterface
    public interface SourcedLogger {

        /**
         * Logs the given message.
         * @param source source of the message
         * @param message the message to log
         */
        void log(@Nullable Console source, String message);

    }

}
