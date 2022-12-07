package me.pesekjak.machine;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import me.pesekjak.machine.auth.OnlineServer;
import me.pesekjak.machine.chat.Messenger;
import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.EntityManager;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.exception.ExceptionHandler;
import me.pesekjak.machine.file.PlayerDataContainer;
import me.pesekjak.machine.file.ServerProperties;
import me.pesekjak.machine.logging.Console;
import me.pesekjak.machine.network.ServerConnection;
import me.pesekjak.machine.server.PlayerManager;
import me.pesekjak.machine.server.schedule.Scheduler;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.WorldManager;
import me.pesekjak.machine.world.biomes.BiomeManager;
import me.pesekjak.machine.world.blocks.BlockManager;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.dimensions.DimensionTypeManager;
import org.jetbrains.annotations.*;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents a Machine server.
 */
@ApiStatus.NonExtendable
public interface Server {

    /**
     * @return server's brand
     */
    @NotNull @NonNls String getBrand();

    /**
     * @return server's implementation version
     */
    @NotNull @NonNls String getImplementationVersion();

    /**
     * @return server's implementation protocol version
     */
    int getImplementationProtocol();

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

    /**
     * Sends a message using server's console
     * @param level logging level of the message
     * @param messages message to send
     */
    default void log(@NotNull Level level, String @NotNull ... messages) {
        getConsole().log(level, messages);
    }

    /**
     * Handles a throwable using server's exception
     * handler.
     * @param throwable throwable to handle
     */
    default void handleThrowable(Throwable throwable) {
        getExceptionHandler().handle(throwable);
    }

    /**
     * @return server's ip adress
     */
    default @NotNull @NonNls String getIp() {
        return getProperties().getServerIp();
    }

    /**
     * @return server's port
     */
    default @Range(from = 0, to = 65536) int getServerPort() {
        return getProperties().getServerPort();
    }

    /**
     * @return max player count
     */
    default int getMaxPlayers() {
        return getProperties().getMaxPlayers();
    }

    /**
     * @return server's view distance
     */
    default int getViewDistance() {
        return getProperties().getViewDistance();
    }

    /**
     * @return server's simulation distance
     */
    default int getSimulationDistance() {
        return getProperties().getSimulationDistance();
    }

    /**
     * @return server's ticks per second
     */
    default int getTps() {
        return getProperties().getTps();
    }

    /**
     * @return how often the server reads the packets sent by client
     */
    default int getServerResponsiveness() {
        return getProperties().getServerResponsiveness();
    }

    /**
     * @return all worlds registered by the server's world manager
     */
    default @Unmodifiable @NotNull Set<World> getWorlds() {
        return getWorldManager().getWorlds();
    }

    /**
     * @param name name of the world
     * @return world with given name
     */
    default @Nullable World getWorld(@NotNull NamespacedKey name) {
        return getWorldManager().getWorld(name);
    }

    /**
     * @return all players on the server
     */
    default @Unmodifiable @NotNull Set<Player> getPlayers() {
        return getPlayerManager().getPlayers();
    }

    /**
     * @param name name of the player
     * @return player with given name
     */
    default @Nullable Player getPlayer(@NotNull String name) {
        return getPlayerManager().getPlayer(name);
    }

    /**
     * @param uuid uuid of the player
     * @return player with given uuid
     */
    default @Nullable Player getPlayer(@NotNull UUID uuid) {
        return getPlayerManager().getPlayer(uuid);
    }

    /**
     * @return all entities registered in server's entity manager
     */
    default @Unmodifiable @NotNull Set<Entity> getEntities() {
        return getEntityManager().getEntities();
    }

    /**
     * @param uuid uuid of the entity
     * @return entity with given uuid in server's entity manager
     */
    default @Nullable Entity getEntity(@NotNull UUID uuid) {
        return getEntityManager().getEntity(uuid);
    }

    /**
     * @param name name of the block type
     * @return block type with given name registered in server's block type manager
     */
    default @Nullable BlockType getBlockType(@NotNull NamespacedKey name) {
        return getBlockManager().getBlockType(name);
    }

}
