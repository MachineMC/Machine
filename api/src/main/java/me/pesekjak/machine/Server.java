package me.pesekjak.machine;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import me.pesekjak.machine.auth.OnlineServer;
import me.pesekjak.machine.chat.Messenger;
import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.entities.EntityManager;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.exception.ExceptionHandler;
import me.pesekjak.machine.file.PlayerDataContainer;
import me.pesekjak.machine.file.ServerProperties;
import me.pesekjak.machine.logging.Console;
import me.pesekjak.machine.network.ServerConnection;
import me.pesekjak.machine.server.PlayerManager;
import me.pesekjak.machine.server.schedule.Scheduler;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.WorldManager;
import me.pesekjak.machine.world.biomes.BiomeManager;
import me.pesekjak.machine.world.blocks.BlockManager;
import me.pesekjak.machine.world.dimensions.DimensionTypeManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Machine server.
 */
@ApiStatus.NonExtendable
public interface Server {

    /**
     * @return server's ticks per second parameter defined in
     * server's properties
     */
    int getTps();

    /**
     * @return how often the server reads the packets sent by
     * client
     */
    int getServerResponsiveness();

    /**
     * @return if the server is running
     */
    boolean isRunning();

    /**
     * @return if the server is in online mode
     */
    boolean isOnline();

    /**
     * @return console implementation used by the server
     */
    @NotNull Console getConsole();

    /**
     * @return exception handler used by the server
     */
    @NotNull ExceptionHandler getExceptionHandler();

    /**
     * @return server's online module if it's in online mode
     */
    @Nullable OnlineServer getOnlineServer();

    /**
     * @return gson formatter used by the server.
     */
    @NotNull Gson getGson();

    /**
     * @return server's properties
     */
    @NotNull ServerProperties getProperties();

    /**
     * @return server's schedule on the main thread
     */
    @NotNull Scheduler getScheduler();

    /**
     * @return server's command dispatcher
     */
    @NotNull CommandDispatcher<CommandExecutor> getCommandDispatcher();

    /**
     * @return server's dimension type manager
     */
    @NotNull DimensionTypeManager getDimensionTypeManager();

    /**
     * @return server's messenger
     */
    @NotNull Messenger getMessenger();

    /**
     * @return server's world manager
     */
    @NotNull WorldManager getWorldManager();

    /**
     * @return server's biome manager
     */
    @NotNull BiomeManager getBiomeManager();

    /**
     * @return server's entity manager
     */
    @NotNull EntityManager getEntityManager();

    /**
     * @return server's player manager
     */
    @NotNull PlayerManager getPlayerManager();

    /**
     * @return server's block manager
     */
    @NotNull BlockManager getBlockManager();

    /**
     * @return server's player data container
     */
    @NotNull PlayerDataContainer getPlayerDataContainer();

    /**
     * @return server's connection
     */
    @NotNull ServerConnection getConnection();

    /**
     * @return server's default world
     */
    @NotNull World getDefaultWorld();

    /**
     * Shuts the server down.
     */
    void shutdown();

}
