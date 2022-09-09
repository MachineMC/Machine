package me.pesekjak.machine.entities;

import me.pesekjak.machine.Machine;

import java.util.UUID;

public class LivingEntity extends Entity {

    public LivingEntity(Machine server, EntityType entityType, UUID uuid) {
        super(server, entityType, uuid);
    }

}
