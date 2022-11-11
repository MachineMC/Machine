package me.pesekjak.machine.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerManager implements ServerProperty {

    private final Map<UUID, Player> playerMap = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    public void addPlayer(Player player) {
        playerMap.putIfAbsent(player.getUuid(), player);
    }

    public void removePlayer(Player player) {
        playerMap.remove(player.getUuid());
    }

    public void removePlayer(UUID uuid) {
        removePlayer(getPlayer(uuid));
    }

    public void removePlayer(String name) {
        removePlayer(getPlayer(name));
    }

    public Player getPlayer(UUID uuid) {
        return playerMap.get(uuid);
    }

    public Player getPlayer(String name) {
        return playerMap.values().stream().filter(player -> player.getName().equals(name))
                .findFirst().orElse(null);
    }

    public Set<Player> getPlayers() {
        return Set.of(playerMap.values().toArray(new Player[0]));
    }

    public Set<Player> getPlayers(Predicate<Player> predicate) {
        return getPlayers().stream().filter(predicate).collect(Collectors.toSet());
    }

}
