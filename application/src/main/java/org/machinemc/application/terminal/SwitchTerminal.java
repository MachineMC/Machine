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
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.logging.Console;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.PlatformConsole;
import org.machinemc.application.ServerContainer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * Terminal that allows switching between multiple running servers.
 */
public abstract class SwitchTerminal extends BaseTerminal {

    @Getter
    private @Nullable ServerContainer current;

    // History of application for the null key
    private final Map<@Nullable ServerContainer, List<String>> messageHistory = new WeakHashMap<>();

    private static final int HISTORY_SIZE = 50;

    public SwitchTerminal(final MachineApplication application,
                          final boolean colors,
                          final InputStream in,
                          final OutputStream out) {
        super(application, colors, in, out);
        messageHistory.put(null, new CopyOnWriteArrayList<>());
    }

    @Override
    public void openServer(final @Nullable ServerContainer container) {
        if (current != null) exitServer(current);
        current = container;
        messageHistory.putIfAbsent(container, new CopyOnWriteArrayList<>());
        refreshHistory(messageHistory.get(container));
    }

    @Override
    public void exitServer(final ServerContainer container) {
        if (current != container) return;
        current = null;
        openServer(null);
    }

    @Override
    protected void log(final SourcedLogger logger,
                       final @Nullable PlatformConsole source,
                       final Level level,
                       final String... messages) {
        final SourcedLogger wrapped = (console, message) -> {
            final Console active;
            if (source != null && source.getSource() != null)
                active = source.getSource().getConsole();
            else
                active = null;

            if (source == null || current != null && active == source)
                logger.log(console, message);

            addToHistory(source != null
                            ? getApplication().container(source.getSource())
                            : null,
                    message);
        };
        super.log(wrapped, source, level, messages);
    }

    private void addToHistory(@Nullable final ServerContainer source, final String message) {
        if (message == null) throw new NullPointerException();
        List<String> history = messageHistory.get(source);
        history.add(message);
        if (history.size() > HISTORY_SIZE)
            history.subList(0, history.size() - HISTORY_SIZE - 1).clear();
        if (!(source == null && current != null)) return;
        history = messageHistory.get(current);
        history.add(message);
        if (history.size() > HISTORY_SIZE)
            history.subList(0, history.size() - HISTORY_SIZE - 1).clear();
    }

    @Override
    public int execute(final String input) {
        final String formatted = CommandExecutor.formatCommandInput(input);
        if (formatted.length() == 0) return 0;
        if (current == null || current.getInstance() == null) return executeApplication(formatted);
        return current.getInstance().getConsole().execute(formatted);
    }

    /**
     * Called when the message history of the previously active
     * server or application should be restored.
     * @param history history
     */
    public abstract void refreshHistory(List<String> history);

}
