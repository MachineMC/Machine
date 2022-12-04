package me.pesekjak.machine.entities.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.pesekjak.machine.auth.MojangAuth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerProfileImpl implements PlayerProfile {

    private String username;
    private final UUID uuid;
    private final PlayerTexturesImpl textures;
    private final boolean online;

    public static PlayerProfileImpl online(@NotNull String username, @NotNull UUID uuid, @Nullable PlayerTexturesImpl textures) {
        return new PlayerProfileImpl(
                username,
                uuid,
                textures,
                true
        );
    }

    public static PlayerProfileImpl offline(@NotNull String username) {
        return new PlayerProfileImpl(
                username,
                MojangAuth.getOfflineUUID(username),
                null,
                false
        );
    }

}
