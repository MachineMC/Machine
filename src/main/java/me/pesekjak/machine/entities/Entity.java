package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.EntityUtils;
import me.pesekjak.machine.world.World;

import java.io.IOException;
import java.util.UUID;

public class Entity implements ServerProperty {

    @Getter
    private final Machine server;

    @Getter
    private final EntityType entityType;
    @Getter
    private final UUID uuid;
    @Getter
    private final int entityId;
    @Getter
    private World world;

    @Getter
    private boolean active;

    public Entity(Machine server, EntityType entityType, UUID uuid) {
        this.server = server;
        this.entityType = entityType;
        this.uuid = uuid;
        this.entityId = EntityUtils.getEmptyID();
        this.world = getServer().getDefaultWorld();
        active = false;
    }

    protected void init() throws IOException {
        if (active)
            throw new IllegalStateException(this + " is already initiated");
        active = true;
        getWorld().getEntityList().add(this);
    }
}
