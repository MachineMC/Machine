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

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.server.Machine;
import org.machinemc.server.logging.DynamicConsole;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;

public class WrappedConsole implements DynamicConsole {

    @Getter @Setter
    private Machine server;
    private final ApplicationTerminal terminal;

    @Getter
    private boolean running;

    public WrappedConsole(final ApplicationTerminal terminal) {
        this.terminal = terminal;
        running = false;
    }

    @Override
    public int execute(final String input) {
        if (!running) return -1;

        final String formatted = CommandExecutor.formatCommandInput(input);
        if (formatted.length() == 0) return 0;
        final ParseResults<CommandExecutor> parse = server.getCommandDispatcher().parse(formatted, this);
        final String[] parts = formatted.split(" ");
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
            sendMessage(TextComponent.of(formatted.substring(0, exception.getCursor()))
                    .append(TextComponent.of(formatted.substring(exception.getCursor())).modify()
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
        terminal.sendMessage(this, sender, message, type);
    }

    @Override
    public void log(final Level level, final String... messages) {
        terminal.log(this, level, messages);
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public InputStream getInputStream() {
        return terminal.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return terminal.getOutputStream();
    }

}
