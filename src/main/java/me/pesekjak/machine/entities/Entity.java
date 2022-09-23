package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.EntityUtils;
import me.pesekjak.machine.world.Location;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import me.pesekjak.machine.world.World;

import java.io.IOException;
import java.util.UUID;

public class Entity implements Identity, ServerProperty {

    @Getter
    private final Machine server;

    @Getter
    private final EntityType entityType;
    @Getter
    private final UUID uuid;
    @Getter
    private final int entityId;
    @Getter @Setter
    private Component displayName = null;
    @Getter @Setter
    private Location location;

    @Getter
    private boolean active;

    public Entity(Machine server, EntityType entityType, UUID uuid) {
        this.server = server;
        this.entityType = entityType;
        this.uuid = uuid;
        entityId = EntityUtils.getEmptyID();
        location = new Location(0, 0, 0, getServer().getDefaultWorld());
        active = false;
    }

    @Override @NotNull
    public UUID uuid() {
        return uuid;
    }

    public World getWorld() {
        return location.getWorld();
    }

    protected void init() throws IOException {
        if (active)
            throw new IllegalStateException(this + " is already initiated");
        active = true;
        getServer().getEntityManager().addEntity(this);
    }

    public void remove() {
        if (!active)
            throw new IllegalStateException(this + " is not active");
        active = false;
        getServer().getEntityManager().removeEntity(this);
    }
}
