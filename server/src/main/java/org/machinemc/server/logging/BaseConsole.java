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
package org.machinemc.server.logging;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.logging.Console;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.util.ChatUtils;
import org.machinemc.server.Machine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;

import static org.machinemc.scriptive.util.ChatUtils.asciiColor;

public abstract class BaseConsole implements Console {

    @Getter
    private final Machine server;
    @Getter @Setter
    private boolean colors;

    @Getter(AccessLevel.PROTECTED)
    private volatile boolean running = false;

    @Getter @Setter
    private @Nullable DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Getter @Setter
    private String
            configPrefix = "CONFIG",
            infoPrefix = "INFO",
            warningPrefix = "WARNING",
            severePrefix = "SEVERE";
    @Getter @Setter
    private @Nullable Colour
            configColor = null,
            infoColor = null,
            warningColor = ChatColor.GOLD,
            severeColor = ChatColor.RED;

    @Getter @Setter
    private String prompt = "> ";

    public BaseConsole(final Machine server, final boolean colors) {
        this.server = server;
        this.colors = colors;
    }

    @Override
    public void start() {
        if (running)
            throw new IllegalStateException("The console is already running");
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public int execute(final String input) {
        final String formattedInput = CommandExecutor.formatCommandInput(input);
        if (formattedInput.length() == 0) return 0;
        final ParseResults<CommandExecutor> parse = server.getCommandDispatcher().parse(formattedInput, this);
        final String[] parts = formattedInput.split(" ");
        try {
            return server.getCommandDispatcher().execute(parse);
        } catch (CommandSyntaxException exception) {
            if (exception.getCursor() == 0) {
                sendMessage(TextComponent.of("Unknown command '" + parts[0] + "'").modify()
                        .color(ChatColor.RED)
                        .finish());
                return -1;
            }
            sendMessage(TextComponent.of(exception.getRawMessage().getString()).modify()
                    .color(ChatColor.RED)
                    .finish());
            sendMessage(TextComponent.of(formattedInput.substring(0, exception.getCursor()))
                    .append(TextComponent.of(formattedInput.substring(exception.getCursor())).modify()
                            .color(ChatColor.RED)
                            .underlined(true)
                            .finish())
                    .append(TextComponent.of("<--[HERE]").modify()
                            .color(ChatColor.RED)
                            .finish()));
            return -1;
        }
    }

    @Override
    public void sendMessage(final @Nullable UUID sender, final Component message, final MessageType type) {
        if (colors)
            info(ChatUtils.consoleFormatted(message));
        else
            info(message.toLegacyString());
    }

    /**
     * Logs the given messages with the specified logging level, using the provided logger.
     *
     * @param logger the logger to use for logging the messages
     * @param level the logging level to use
     * @param messages the messages to log
     */
    protected void log(final Logger logger, final Level level, final String... messages) {
        final String prefix = getPrefix(level);
        final String date = now();
        for (final String message : messages) {
            final String formatted = colors ? "[" + date + "] " + prefix + ChatUtils.consoleFormatted(message)
                    + ServerConsole.RESET : "[" + date + "] " + prefix + message;
            logger.log(formatted);
        }
    }

    /**
     * Returns the logging prefix for the given logging level.
     *
     * @param level the logging level to get the prefix for
     * @return the logging prefix for the given level
     */
    protected String getPrefix(final Level level) {
        return switch (level.intValue()) {
            case 700 -> (colors && configColor != null ? asciiColor(configColor) : ServerConsole.EMPTY)
                    + configPrefix + ": "; // Config value
            case 800 -> (colors && infoColor != null ? asciiColor(infoColor) : ServerConsole.EMPTY)
                    + infoPrefix + ": "; // Info value
            case 900 -> (colors && warningColor != null ? asciiColor(warningColor) : ServerConsole.EMPTY)
                    + warningPrefix + ": "; // Warning value
            case 1000 -> (colors && severeColor != null ? asciiColor(severeColor) : ServerConsole.EMPTY)
                    + severePrefix + ": "; // Severe value
            default -> "";
        };
    }

    /**
     * Returns a current date using console's date formatter.
     * @return formatted date
     */
    protected String now() {
        return dateFormatter != null ? dateFormatter.format(LocalDateTime.now()) : ServerConsole.EMPTY;
    }

    @FunctionalInterface
    protected interface Logger {

        /**
         * Logs the given message.
         *
         * @param message the message to log
         */
        void log(String message);

    }

}
