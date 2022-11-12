package me.pesekjak.machine.logging;

import com.mojang.brigadier.ParseResults;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.ChatColor;
import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.server.ServerProperty;
import net.kyori.adventure.text.format.TextColor;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ConsoleHighlighter implements Highlighter, ServerProperty {

    @Getter
    private final Machine server;
    @Getter
    private final ServerConsole console;

    @Getter @Setter
    private TextColor
            knownColor = ChatColor.DARK_CYAN.asStyle().color(),
            unknownColor = ChatColor.RED.asStyle().color();

    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        final AttributedStringBuilder sb = new AttributedStringBuilder();
        if(console.isColors()) {
            final ParseResults<CommandExecutor> result = server.getCommandDispatcher().parse(CommandExecutor.formatCommandInput(buffer), console);
            final TextColor color = result.getReader().canRead() ? unknownColor : knownColor;
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
