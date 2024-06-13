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
package org.machinemc.server.server;

import lombok.Getter;
import org.machinemc.api.Server;
import org.machinemc.api.entities.Player;
import org.machinemc.api.server.PlayerManager;

import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default player manager implementation.
 */
public class ServerPlayerManager implements PlayerManager {

    private final Map<UUID, Player> playerMap = new ConcurrentHashMap<>();
    @Getter
    private final Server server;

    public ServerPlayerManager(final Server server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
    }

    @Override
    public void addPlayer(final Player player) {
        Objects.requireNonNull(player, "Player can not be null");
        playerMap.putIfAbsent(player.getUUID(), player);
    }

    @Override
    public void removePlayer(final Player player) {
        Objects.requireNonNull(player, "Player can not be null");
        playerMap.remove(player.getUUID());
    }

    @Override
    public Optional<Player> getPlayer(final UUID uuid) {
        Objects.requireNonNull(uuid, "UUID of player can not be null");
        return Optional.ofNullable(playerMap.get(uuid));
    }

    @Override
    public Optional<Player> getPlayer(final String name) {
        Objects.requireNonNull(name, "Name of player can not be null");
        return playerMap.values().stream()
                .filter(player -> player.getName().equals(name))
                .findFirst();
    }

    @Override
    public Set<Player> getPlayers() {
        return Set.of(playerMap.values().toArray(new Player[0]));
    }

    @Override
    public Set<Player> getPlayers(final Predicate<Player> predicate) {
        Objects.requireNonNull(predicate, "Predicate can not be null");
        return getPlayers().stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "ServerPlayerManager("
                + "server=" + server
                + ')';
    }
}
