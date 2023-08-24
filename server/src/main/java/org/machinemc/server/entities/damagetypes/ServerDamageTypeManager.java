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
package org.machinemc.server.entities.damagetypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.Server;
import org.machinemc.api.entities.damagetypes.DamageType;
import org.machinemc.api.entities.damagetypes.DamageTypeManager;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of the dimension manager.
 */
@RequiredArgsConstructor
public class ServerDamageTypeManager implements DamageTypeManager {

    protected final AtomicInteger idCounter = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:damage_type";

    private final Map<Integer, DamageType> damageTypes = new ConcurrentHashMap<>();
    @Getter
    private final Server server;

    /**
     * Creates damage type manager with default values.
     * @param server server
     * @return new manager
     */
    public static DamageTypeManager createDefault(final Server server) {
        final ServerDamageTypeManager manager = new ServerDamageTypeManager(server);
        for (final DamageType damageType : ServerDamageType.createDefaults())
            manager.addDamageType(damageType);
        return manager;
    }

    @Override
    public void addDamageType(final DamageType damageType) {
        Objects.requireNonNull(damageType, "Damage type can not be null");
        if (isRegistered(damageType.getName()))
            throw new IllegalStateException("Damage type '" + damageType.getName() + "' is already registered");
        damageTypes.put(idCounter.getAndIncrement(), damageType);
    }

    @Override
    public boolean removeDamageType(final DamageType damageType) {
        Objects.requireNonNull(damageType, "Damage type can not be null");
        return damageTypes.remove(getDamageTypeID(damageType)) == null;
    }

    @Override
    public boolean isRegistered(final DamageType damageType) {
        Objects.requireNonNull(damageType, "Damage type can not be null");
        return damageTypes.containsValue(damageType);
    }

    @Override
    public Optional<DamageType> getDamageType(final NamespacedKey name) {
        for (final DamageType damageType : getDamageTypes()) {
            if (!(damageType.getName().equals(name))) continue;
            return Optional.of(damageType);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DamageType> getByID(final int id) {
        return Optional.ofNullable(damageTypes.get(id));
    }

    @Override
    public int getDamageTypeID(final DamageType damageType) {
        Objects.requireNonNull(damageType, "Damage type can not be null");
        for (final Map.Entry<Integer, DamageType> entry : damageTypes.entrySet()) {
            if (entry.getValue().equals(damageType))
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public Set<DamageType> getDamageTypes() {
        return damageTypes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NBTCompound getDamageTypeNBT(final DamageType damageType) {
        Objects.requireNonNull(damageType, "Damage type can not be null");
        if (!isRegistered(damageType))
            throw new IllegalStateException();
        final NBTCompound nbtCompound = damageType.toNBT();
        return new NBTCompound(Map.of(
                "name", damageType.getName().toString(),
                "id", getDamageTypeID(damageType),
                "element", nbtCompound
        ));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBTCompound> getCodecElements() {
        return damageTypes.values().stream()
                .map(this::getDamageTypeNBT)
                .toList();
    }

    @Override
    public String toString() {
        return "ServerDamageTypeManager("
                + "server=" + server
                + ')';
    }

}
