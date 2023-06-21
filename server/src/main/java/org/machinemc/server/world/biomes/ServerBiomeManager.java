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
package org.machinemc.server.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.Server;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of biome manager.
 */
@RequiredArgsConstructor
public class ServerBiomeManager implements BiomeManager {

    protected final AtomicInteger idCounter = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:worldgen/biome";

    private final Map<Integer, Biome> biomes = new ConcurrentHashMap<>();
    @Getter
    private final Server server;

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static BiomeManager createDefault(final Server server) {
        final ServerBiomeManager manager = new ServerBiomeManager(server);
        manager.addBiome(ServerBiome.createDefault());
        return manager;
    }

    @Override
    public void addBiome(final Biome biome) {
        if (isRegistered(biome.getName()))
            throw new IllegalStateException("Biome '" + biome.getName() + "' is already registered");
        biomes.put(idCounter.getAndIncrement(), biome);
    }

    @Override
    public boolean removeBiome(final Biome biome) {
        return biomes.remove(getBiomeID(biome)) == null;
    }

    @Override
    public boolean isRegistered(final Biome biome) {
        return biomes.containsValue(biome);
    }

    @Override
    public @Nullable Biome getBiome(final NamespacedKey name) {
        for (final Biome biome : getBiomes()) {
            if (!(biome.getName().equals(name))) continue;
            return biome;
        }
        return null;
    }

    @Override
    public @Nullable Biome getByID(final int id) {
        return biomes.get(id);
    }

    @Override
    public int getBiomeID(final Biome biome) {
        for (final Map.Entry<Integer, Biome> entry : biomes.entrySet()) {
            if (entry.getValue().equals(biome))
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public Set<Biome> getBiomes() {
        return biomes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NBTCompound getBiomeNBT(final Biome biome) {
        if (!isRegistered(biome))
            throw new IllegalStateException();
        final NBTCompound nbtCompound = biome.toNBT();
        return new NBTCompound(Map.of(
                "name", biome.getName().toString(),
                "id", getBiomeID(biome),
                "element", nbtCompound
        ));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBTCompound> getCodecElements() {
        return new ArrayList<>(biomes.values().stream()
                .map(this::getBiomeNBT)
                .toList());
    }

    @Override
    public String toString() {
        return "ServerBiomeManager("
                + "server=" + server
                + ')';
    }

}
