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
package org.machinemc.server.terminal.smart;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SmartHighlighter implements Highlighter {

    private final SmartTerminal terminal;

    @Getter @Setter
    private Colour
            knownColor = ChatColor.DARK_AQUA,
            unknownColor = ChatColor.RED;

    @Override
    public AttributedString highlight(final LineReader reader, final String buffer) {
        final AttributedStringBuilder sb = new AttributedStringBuilder();

        if (!terminal.isColored()) return sb.append(buffer).toAttributedString();

        final CommandDispatcher<CommandExecutor> dispatcher = terminal.getServer().getCommandDispatcher();
        if (dispatcher == null) return sb.append(buffer).toAttributedString();

        final ParseResults<CommandExecutor> result = dispatcher.parse(CommandExecutor.formatCommandInput(buffer), terminal);
        final Colour color = result.getReader().canRead() ? unknownColor : knownColor;
        if (color != null)
            sb.style(new AttributedStyle().foreground(color.getRed(), color.getGreen(), color.getBlue()));

        sb.append(buffer);
        return sb.toAttributedString();
    }

    @Override
    public void setErrorPattern(final Pattern errorPattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setErrorIndex(final int errorIndex) {
        throw new UnsupportedOperationException();
    }

}
