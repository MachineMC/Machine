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

import lombok.Builder;
import lombok.Getter;
import org.machinemc.api.entities.damagetypes.DamageType;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;

import java.util.Locale;
import java.util.Map;

@Getter
@Builder
public class ServerDamageType implements DamageType {

    private final NamespacedKey name;
    private final Scaling scaling;
    private final float exhaustion;
    private final String messageID;
    @Builder.Default private final Effects effects = Effects.HURT;
    @Builder.Default private final DeathMessageType deathMessageType = DeathMessageType.DEFAULT;

    /**
     * Creates the default damage types.
     * @return default damage types
     */
    public static DamageType[] createDefaults() {
        return new DamageType[]{
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("arrow"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("arrow")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("bed_respawn_point"))
                        .scaling(Scaling.ALWAYS)
                        .exhaustion(0.1f)
                        .messageID("bedRespawnPoint")
                        .deathMessageType(DeathMessageType.INTENTIONAL_GAME_DESIGN)
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("cactus"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("cactus")
                        .deathMessageType(DeathMessageType.INTENTIONAL_GAME_DESIGN)
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("cramming"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("cramming")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("dragon_breath"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("dragonBreath")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("drown"))
                        .effects(Effects.DROWNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("drown")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("dry_out"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("dryout")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("explosion"))
                        .scaling(Scaling.ALWAYS)
                        .exhaustion(0.1f)
                        .messageID("explosion")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("fall"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("fall")
                        .deathMessageType(DeathMessageType.FALL_VARIANTS)
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("falling_anvil"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("anvil")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("falling_block"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("fallingBlock")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("falling_stalactite"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("fallingStalactite")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("fireball"))
                        .effects(Effects.BURNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("fireball")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("fireworks"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("fireworks")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("fly_into_wall"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("flyIntoWall")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("freeze"))
                        .effects(Effects.FREEZING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("freeze")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("generic"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("generic")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("generic_kill"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("genericKill")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("hot_floor"))
                        .effects(Effects.BURNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("hotFloor")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("in_fire"))
                        .effects(Effects.BURNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("inFire")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("in_wall"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("inWall")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("indirect_magic"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("indirectMagic")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("lava"))
                        .effects(Effects.BURNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("lava")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("lightning_bolt"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("lightningBolt")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("magic"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("magic")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("mob_attack"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("mob")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("mob_attack_no_aggro"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("mob")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("mob_projectile"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("mob")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("on_fire"))
                        .effects(Effects.BURNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("onFire")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("out_of_world"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("outOfWorld")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("outside_border"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("outsideBorder")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("player_attack"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("player")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("player_explosion"))
                        .scaling(Scaling.ALWAYS)
                        .exhaustion(0.1f)
                        .messageID("explosion.player")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("sonic_boom"))
                        .scaling(Scaling.ALWAYS)
                        .exhaustion(0.0f)
                        .messageID("sonic_boom")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("stalagmite"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("stalagmite")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("starve"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("starve")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("sting"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("sting")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("sweet_berry_bush"))
                        .effects(Effects.POKING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("sweetBerryBush")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("thorns"))
                        .effects(Effects.THORNS)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("thorns")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("thrown"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("thrown")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("trident"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("trident")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("unattributed_fireball"))
                        .effects(Effects.BURNING)
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("onFire")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("wither"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.0f)
                        .messageID("wither")
                        .build(),
                ServerDamageType.builder()
                        .name(NamespacedKey.minecraft("wither_skull"))
                        .scaling(Scaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER)
                        .exhaustion(0.1f)
                        .messageID("witherSkull")
                        .build(),
        };
    }

    @Override
    public NBTCompound toNBT() {
        return new NBTCompound(Map.of(
                "scaling", scaling.name().toLowerCase(Locale.ENGLISH),
                "exhaustion", exhaustion,
                "message_id", messageID,
                "effects", effects.name().toLowerCase(Locale.ENGLISH),
                "death_message_type", deathMessageType.name().toLowerCase(Locale.ENGLISH)
        ));
    }

}
