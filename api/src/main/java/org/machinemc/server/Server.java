package org.machinemc.server;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import org.machinemc.api.auth.OnlineServer;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.EntityManager;
import org.machinemc.api.entities.Player;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.file.PlayerDataContainer;
import org.machinemc.api.file.ServerProperties;
import org.machinemc.api.inventory.Item;
import org.machinemc.api.logging.Console;
import org.machinemc.api.network.ServerConnection;
import org.machinemc.api.server.PlayerManager;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.Material;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldManager;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeManager;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.dimensions.DimensionTypeManager;
import org.machinemc.api.world.particles.Particle;
import org.machinemc.api.world.particles.ParticleType;
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
     * Creates new instance of the classic buffer implementation.
     * @return new default server buffer
     * @throws UnsupportedOperationException if the creator hasn't been initialized
     */
    static ServerBuffer createServerBuffer() {
        if(Factories.BUFFER_FACTORY == null)
            throw new UnsupportedOperationException();
        return Factories.BUFFER_FACTORY.create();
    }

    /**
     * Creates new instance of the classic item implementation.
     * @param material material of the item
     * @param amount amount of the item
     * @return new item
     * @throws UnsupportedOperationException if the creator hasn't been initialized
     * @throws IllegalStateException if the material can't have item form
     */
    static Item createItem(Material material, byte amount) {
        if(Factories.ITEM_FACTORY == null)
            throw new UnsupportedOperationException();
        return Factories.ITEM_FACTORY.create(material, amount);
    }

    /**
     * Creates new instance of the classic item implementation with the amount
     * of 1.
     * @param material material of the item
     * @return new item
     * @throws UnsupportedOperationException if the creator hasn't been initialized
     * @throws IllegalStateException if the material can't have item form
     */
    static Item createItem(Material material) {
        return createItem(material, (byte) 1);
    }

    /**
     * Creates new instance of the classic particle implementation.
     * @param type type of the particle
     * @return new particle
     * @throws UnsupportedOperationException if the creator hasn't been initialized
     */
    static Particle createParticle(ParticleType type) {
        if(Factories.PARTICLE_FACTORY == null)
            throw new UnsupportedOperationException();
        return Factories.PARTICLE_FACTORY.create(type);
    }

    /**
     * @return server's brand
     */
    String getBrand();

    /**
     * @return server's implementation version
     */
    String getImplementationVersion();

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
    Console getConsole();

    /**
     * @return exception handler used by the server
     */
    ExceptionHandler getExceptionHandler();

    /**
     * @return server's online module if it's in online mode
     */
    OnlineServer getOnlineServer();

    /**
     * @return gson formatter used by the server.
     */
    Gson getGson();

    /**
     * @return server's properties
     */
    ServerProperties getProperties();

    /**
     * @return server's schedule on the main thread
     */
    Scheduler getScheduler();

    /**
     * @return server's command dispatcher
     */
    CommandDispatcher<CommandExecutor> getCommandDispatcher();

    /**
     * @return server's dimension type manager
     */
    DimensionTypeManager getDimensionTypeManager();

    /**
     * @return server's messenger
     */
    Messenger getMessenger();

    /**
     * @return server's world manager
     */
    WorldManager getWorldManager();

    /**
     * @return server's biome manager
     */
    BiomeManager getBiomeManager();

    /**
     * @return server's entity manager
     */
    EntityManager getEntityManager();

    /**
     * @return server's player manager
     */
    PlayerManager getPlayerManager();

    /**
     * @return server's block manager
     */
    BlockManager getBlockManager();

    /**
     * @return server's player data container
     */
    PlayerDataContainer getPlayerDataContainer();

    /**
     * @return server's connection
     */
    ServerConnection getConnection();

    /**
     * @return server's default world
     */
    World getDefaultWorld();

    /**
     * Shuts the server down.
     */
    void shutdown();

    /**
     * Sends a message using server's console
     * @param level logging level of the message
     * @param messages message to send
     */
    default void log(Level level, String... messages) {
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
    default String getIp() {
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
    default @Unmodifiable Set<World> getWorlds() {
        return getWorldManager().getWorlds();
    }

    /**
     * @param name name of the world
     * @return world with given name
     */
    default @Nullable World getWorld(NamespacedKey name) {
        return getWorldManager().getWorld(name);
    }

    /**
     * @return all players on the server
     */
    default @Unmodifiable Set<Player> getPlayers() {
        return getPlayerManager().getPlayers();
    }

    /**
     * @param name name of the player
     * @return player with given name
     */
    default @Nullable Player getPlayer(String name) {
        return getPlayerManager().getPlayer(name);
    }

    /**
     * @param uuid uuid of the player
     * @return player with given uuid
     */
    default @Nullable Player getPlayer(UUID uuid) {
        return getPlayerManager().getPlayer(uuid);
    }

    /**
     * @return all entities registered in server's entity manager
     */
    default @Unmodifiable Set<Entity> getEntities() {
        return getEntityManager().getEntities();
    }

    /**
     * @param uuid uuid of the entity
     * @return entity with given uuid in server's entity manager
     */
    default @Nullable Entity getEntity(UUID uuid) {
        return getEntityManager().getEntity(uuid);
    }

    /**
     * @param name name of the block type
     * @return block type with given name registered in server's block type manager
     */
    default @Nullable BlockType getBlockType(NamespacedKey name) {
        return getBlockManager().getBlockType(name);
    }

    default @Nullable Biome getBiome(NamespacedKey name) {
        return getBiomeManager().getBiome(name);
    }

}
