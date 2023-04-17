package org.machinemc.server.logging;

import com.mojang.brigadier.ParseResults;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.server.Machine;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.regex.Pattern;

/**
 * Default console highlighter for Machine server.
 */
@RequiredArgsConstructor
public class ConsoleHighlighter implements Highlighter, ServerProperty {

    @Getter
    private final Machine server;
    @Getter
    private final ServerConsole console;

    @Getter @Setter
    private @Nullable Colour
            knownColor = ChatColor.DARK_AQUA,
            unknownColor = ChatColor.RED;

    @Override
    public AttributedString highlight(final LineReader reader, final String buffer) {
        final AttributedStringBuilder sb = new AttributedStringBuilder();
        if (console.isColors()) {
            final ParseResults<CommandExecutor> result = server.getCommandDispatcher()
                    .parse(CommandExecutor.formatCommandInput(buffer), console);
            final Colour color = result.getReader().canRead() ? unknownColor : knownColor;
            if (color != null)
                sb.style(new AttributedStyle().foreground(color.getRed(), color.getGreen(), color.getBlue()));
        }
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
