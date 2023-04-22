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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import org.machinemc.server.Machine;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.server.ServerProperty;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

/**
 * Default console completer for Machine server.
 * @param server server
 * @param console console
 */
public record ConsoleCompleter(Machine server, ServerConsole console) implements Completer, ServerProperty {

    @Override
    public void complete(final LineReader reader, final ParsedLine line, final List<Candidate> candidates) {
        final CommandDispatcher<CommandExecutor> dispatcher = server.getCommandDispatcher();
        if (line.wordIndex() == 0) {
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
    public Machine getServer() {
        return server;
    }

}
