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
package org.machinemc.api.particles;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;
import org.machinemc.nbt.exceptions.NBTException;

import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

/**
 * Particle options implementation for vibration particles.
 */
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VibrationParticleOption implements ParticleOption {

    private static final PositionSource DEFAULT_POSITION = new BlockPositionSource(new BlockPosition(0, 0, 0));

    private @Nullable PositionSource destination;
    @Getter
    private int arrivalInTicks = 1;

    @Override
    public void load(final NBTCompound compound) {
        Objects.requireNonNull(compound, "Source compound can not be null");
        if (compound.containsKey("arrival_in_ticks") && compound.getNBT("arrival_in_ticks").tag() == NBT.Tag.INT)
            arrivalInTicks = compound.getValue("arrival_in_ticks");

        if (!compound.containsKey("destination") || compound.getNBT("destination").tag() != NBT.Tag.COMPOUND)
            return;

        final NBTCompound destinationCompound = compound.getNBT("destination");
        if (!destinationCompound.containsKey("type")
                || destinationCompound.getNBT("type").tag() != NBT.Tag.STRING)
            return;
        final PositionSourceType positionSourceType;
        try {
            positionSourceType = PositionSourceType.get(NamespacedKey.parse(destinationCompound.getValue("type")))
                    .orElse(null);
            if (positionSourceType == PositionSourceType.BLOCK)
                destination = BlockPositionSource.create(destinationCompound);
            else if (positionSourceType == PositionSourceType.ENTITY)
                destination = EntityPositionSource.create(destinationCompound);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        compound.set("arrival_in_ticks", arrivalInTicks);
        final PositionSource position = destination != null ? destination : DEFAULT_POSITION;
        final NBTCompound destinationCompound = new NBTCompound();
        destinationCompound.set("type", position.getType().name.toString());
        position.toNBT().forEach(destinationCompound::set);
        compound.set("destination", destinationCompound);
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        final PositionSource destination = this.destination != null ? this.destination : DEFAULT_POSITION;
        buf.writeNamespacedKey(destination.getType().name);
        destination.write(buf);
        buf.writeVarInt(arrivalInTicks);
    }

    /**
     * @return the destination of the vibration particles
     */
    public Optional<PositionSource> getDestination() {
        return Optional.ofNullable(destination);
    }

    /**
     * Represents a source of the vibration.
     */
    public sealed interface PositionSource
            extends NBTSerializable, Writable
            permits BlockPositionSource, EntityPositionSource {

        /**
         * @return type of the vibration source
         */
        PositionSourceType getType();

    }

    /**
     * Block vibration source.
     * @param position position of the source block
     */
    public record BlockPositionSource(BlockPosition position) implements PositionSource {

        /**
         * Creates new block position source from a given nbt compound.
         * @see BlockPositionSource#toNBT()
         * @param source nbt of the position source
         * @return position source from the nbt
         * @throws NBTException if the nbt is invalid
         */
        public static BlockPositionSource create(final NBTCompound source) {
            Objects.requireNonNull(source, "Source compound can not be null");
            if (!source.containsKey("pos") || source.getNBT("pos").tag() != NBT.Tag.LIST)
                throw new NBTException("NBT source does not contain valid position information");
            final NBTList list = source.getNBT("pos");
            if (list.size() < 3) throw new NBTException("NBT source does not contain valid position information");
            return new BlockPositionSource(new BlockPosition(
                    list.getValue(0),
                    list.getValue(1),
                    list.getValue(2)
            ));
        }

        public BlockPositionSource {
            Objects.requireNonNull(position);
        }

        @Override
        public PositionSourceType getType() {
            return PositionSourceType.BLOCK;
        }

        @Override
        public NBTCompound toNBT() {
            final NBTList list = new NBTList();
            list.add(NBT.convert(position.getX()));
            list.add(NBT.convert(position.getY()));
            list.add(NBT.convert(position.getZ()));
            return new NBTCompound(Map.of("pos", list));
        }

        @Override
        public void write(final ServerBuffer buf) {
            Objects.requireNonNull(buf);
            buf.writeBlockPos(position);
        }

    }

    /**
     * Entity vibration source.
     * @param uuid uuid of the entity source
     * @param entityID entity id of the entity
     * @param offset offset of the particle from entity's feet
     * @see EntityPositionSource#EntityPositionSource(Entity)
     */
    public record EntityPositionSource(UUID uuid, int entityID, float offset) implements PositionSource {

        /**
         * Creates new entity position source from a given nbt compound.
         * @see EntityPositionSource#toNBT()
         * @param source nbt of the position source
         * @return position source from the nbt
         * @throws NBTException if the nbt is invalid
         */
        public static EntityPositionSource create(final NBTCompound source) {
            Objects.requireNonNull(source, "Source compound can not be null");
            if (!source.containsKey("y_offset") || source.getNBT("y_offset").tag() != NBT.Tag.FLOAT
                    || !source.containsKey("source_entity") || source.getNBT("source_entity").tag() != NBT.Tag.LIST)
                throw new NBTException("NBT source does not contain valid position information");

            final float offset = source.getValue("y_offset");
            final NBTList entity = source.getNBT("source_entity");
            final int entityID;
            if (!source.containsKey("entity_id") || source.getNBT("entity_id").tag() != NBT.Tag.INT)
                entityID = -1;
            else
                entityID = source.getValue("entity_id");

            if (entity.size() < 4) throw new NBTException("NBT source does not contain valid position information");
            final ByteBuf buf = Unpooled.buffer();
            for (int i = 0; i < 4; i++)
                buf.writeInt(entity.getValue(i));
            final UUID uuid = new UUID(buf.readLong(), buf.readLong());

            return new EntityPositionSource(uuid, entityID, offset);
        }

        public EntityPositionSource {
            Objects.requireNonNull(uuid, "UUID of the entity position source can not be null");
        }

        public EntityPositionSource(final Entity entity) {
            this(entity.getUUID(), entity.getEntityID(), (float) entity.getEntityType().getHeight());
        }

        @Override
        public PositionSourceType getType() {
            return PositionSourceType.ENTITY;
        }

        @Override
        public NBTCompound toNBT() {
            final NBTList list = new NBTList();
            final ByteBuf buf = Unpooled.buffer();
            buf.writeLong(uuid.getMostSignificantBits()).writeLong(uuid.getLeastSignificantBits());
            for (int i = 0; i < 4; i++)
                list.add(NBT.convert(buf.readInt()));
            return new NBTCompound(Map.of(
                    "y_offset", offset,
                    "source_entity", list,
                    "entity_id", entityID // added by Machine for deserialization
            ));
        }

        @Override
        public void write(final ServerBuffer buf) {
            Objects.requireNonNull(buf);
            buf.writeVarInt(entityID);
            buf.writeFloat(offset);
        }

    }

    /**
     * Type of source for a vibration.
     */
    @AllArgsConstructor
    public enum PositionSourceType {

        BLOCK(NamespacedKey.minecraft("block")),
        ENTITY(NamespacedKey.minecraft("entity"));

        @Getter
        private final NamespacedKey name;

        /**
         * Returns position source type with given namespaced key.
         * @param name name of the position source type
         * @return position source type with given name
         */
        public static Optional<PositionSourceType> get(final NamespacedKey name) {
            Objects.requireNonNull(name, "Name of the position source type can not be null");
            for (final PositionSourceType type : PositionSourceType.values())
                if (type.name.equals(name)) return Optional.of(type);
            return Optional.empty();
        }

    }

}
