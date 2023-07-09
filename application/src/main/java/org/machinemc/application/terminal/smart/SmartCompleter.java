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
package org.machinemc.application.terminal.smart;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import lombok.RequiredArgsConstructor;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.RunnableServer;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SmartCompleter implements Completer {

    private final SmartTerminal terminal;
    private final Supplier<RunnableServer> server;

    @Override
    public void complete(final LineReader reader, final ParsedLine line, final List<Candidate> candidates) {
        if (server.get() == null) {
            final CommandDispatcher<MachineApplication> dispatcher = terminal.getApplication().getCommandDispatcher();
            if (dispatcher == null) return;
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
                dispatcher.getCompletionSuggestions(dispatcher.parse(text, terminal.getApplication()))
                        .thenAccept(suggestions -> candidates.addAll(suggestions.getList().stream()
                                .map(Suggestion::getText)
                                .map(Candidate::new)
                                .toList()));
            }
            return;
        }

        final CommandDispatcher<CommandExecutor> dispatcher = server.get().getCommandDispatcher();
        if (dispatcher == null) return;
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
            dispatcher.getCompletionSuggestions(dispatcher.parse(text, server.get().getConsole()))
                    .thenAccept(suggestions -> candidates.addAll(suggestions.getList().stream()
                            .map(Suggestion::getText)
                            .map(Candidate::new)
                            .toList()));
        }
    }

}
