package org.machinemc.server.logging;

import com.mojang.brigadier.ParseResults;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.machinemc.server.Machine;
import org.machinemc.api.chat.ChatColor;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.server.ServerProperty;
import net.kyori.adventure.text.format.TextColor;
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
    private @Nullable TextColor
            knownColor = ChatColor.DARK_CYAN.asStyle().color(),
            unknownColor = ChatColor.RED.asStyle().color();

    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        final AttributedStringBuilder sb = new AttributedStringBuilder();
        if(console.isColors()) {
            final ParseResults<CommandExecutor> result = server.getCommandDispatcher().parse(CommandExecutor.formatCommandInput(buffer), console);
            final TextColor color = result.getReader().canRead() ? unknownColor : knownColor;
            if(color != null)
                sb.style(new AttributedStyle().foreground(color.red(), color.green(), color.blue()));
        }
        sb.append(buffer);
        return sb.toAttributedString();
    }

    @Override
    public void setErrorPattern(Pattern errorPattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setErrorIndex(int errorIndex) {
        throw new UnsupportedOperationException();
    }

}
