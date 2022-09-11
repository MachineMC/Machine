package me.pesekjak.machine.entities;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.EntityUtils;

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
    @Getter @Setter
    private String displayName = null;

    @Getter @Setter
    private boolean active;

    public Entity(Machine server, EntityType entityType, UUID uuid) {
        this.server = server;
        this.entityType = entityType;
        this.uuid = uuid;
        this.entityId = EntityUtils.getEmptyID();
        active = false;
    }

}
