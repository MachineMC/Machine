package me.pesekjak.machine.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.pesekjak.machine.Machine;

public final class ServerCommands {

    private ServerCommands() {
        throw new UnsupportedOperationException();
    }

    public static void register(Machine server, CommandDispatcher<CommandExecutor> dispatcher) {
        dispatcher.register(stopCommand(server));
    }

    private static LiteralArgumentBuilder<CommandExecutor> stopCommand(Machine server) {
        LiteralArgumentBuilder<CommandExecutor> stopCommand = LiteralArgumentBuilder.literal("stop");
        stopCommand.executes(c -> {
            server.shutdown();
            return 0;
        });
        return stopCommand;
    }

}