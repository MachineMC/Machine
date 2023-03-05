package org.machinemc.server.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.server.PlayerManager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default player manager implementation.
 */
@RequiredArgsConstructor
public class PlayerManagerImpl implements PlayerManager {

    private final Map<UUID, Player> playerMap = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    @Override
    public void addPlayer(@NotNull Player player) {
        playerMap.putIfAbsent(player.getUuid(), player);
    }

    @Override
    public void removePlayer(@NotNull Player player) {
        playerMap.remove(player.getUuid());
    }

    @Override
    public Player getPlayer(@NotNull UUID uuid) {
        return playerMap.get(uuid);
    }

    @Override
    public Player getPlayer(@NotNull String name) {
        return playerMap.values().stream().filter(player -> player.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    public @NotNull Set<Player> getPlayers() {
        return Set.of(playerMap.values().toArray(new Player[0]));
    }

    @Override
    public @NotNull Set<Player> getPlayers(@NotNull Predicate<Player> predicate) {
        return getPlayers().stream().filter(predicate).collect(Collectors.toSet());
    }

}
