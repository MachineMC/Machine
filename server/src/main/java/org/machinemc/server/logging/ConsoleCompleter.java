package org.machinemc.server.logging;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import org.machinemc.server.Machine;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

/**
 * Default console completer for Machine server.
 */
public record ConsoleCompleter(Machine server, ServerConsole console) implements Completer, ServerProperty {

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        final CommandDispatcher<CommandExecutor> dispatcher = server.getCommandDispatcher();
        if(line.wordIndex() == 0) {
            final String commandString = line.word().toLowerCase();
            candidates.addAll(dispatcher.getRoot().getChildren().stream()
                    .map(CommandNode::getName)
                    .filter(name -> commandString.isBlank() || name.toLowerCase().startsWith(commandString))
                    .map(Candidate::new)
                    .toList()
            );
        } else {
            final String text = line.line();
            dispatcher.getCompletionSuggestions(dispatcher.parse(text, console))
                    .thenAccept(suggestions -> candidates.addAll(suggestions.getList().stream()
                            .map(Suggestion::getText)
                            .map(Candidate::new)
                            .toList()));
        }
    }

    @Override
    public @NotNull Machine getServer() {
        return server;
    }

}
