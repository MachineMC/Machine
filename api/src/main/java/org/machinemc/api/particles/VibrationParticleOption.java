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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import java.util.UUID;

/**
 * Particle options implementation for vibration particles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VibrationParticleOption implements ParticleOption {

    private static final PositionSource DEFAULT_POSITION = new BlockPositionSource(new BlockPosition(0, 0, 0));

    private @Nullable PositionSource destination;
    private int arrivalInTicks = 1;

    @Override
    public void load(final NBTCompound compound) {
        if (compound.containsKey("arrival_in_ticks") && compound.get("arrival_in_ticks").tag() == NBT.Tag.INT)
            arrivalInTicks = compound.get("arrival_in_ticks").value();

        if (!compound.containsKey("destination") || compound.get("destination").tag() != NBT.Tag.COMPOUND)
            return;

        final NBTCompound destinationCompound = (NBTCompound) compound.get("destination");
        if (!destinationCompound.containsKey("type")
                || destinationCompound.get("type").tag() != NBT.Tag.STRING)
            return;
        final PositionSourceType positionSourceType;
        try {
            positionSourceType = PositionSourceType.get(NamespacedKey.parse(destinationCompound.get("type").value()));
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
        compound.put("arrival_in_ticks", NBT.convert(arrivalInTicks));
        final PositionSource position = destination != null ? destination : DEFAULT_POSITION;
        final NBTCompound destinationCompound = new NBTCompound();
        destinationCompound.put("type", position.getType().name.toString());
        destinationCompound.putAll(position.toNBT());
        compound.put("destination", destinationCompound);
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        final PositionSource destination = this.destination != null ? this.destination : DEFAULT_POSITION;
        buf.writeNamespacedKey(destination.getType().name);
        destination.write(buf);
        buf.writeVarInt(arrivalInTicks);
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
            if (!source.containsKey("pos") || source.get("pos").tag() != NBT.Tag.LIST)
                throw new NBTException();
            final NBTList list = source.getList("pos");
            if (list.size() < 3) throw new NBTException();
            return new BlockPositionSource(new BlockPosition(
                    list.get(0).value(),
                    list.get(1).value(),
                    list.get(2).value()
            ));
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
            if (!source.containsKey("y_offset") || source.get("y_offset").tag() != NBT.Tag.FLOAT
                    || !source.containsKey("source_entity") || source.get("source_entity").tag() != NBT.Tag.LIST)
                throw new NBTException();

            final float offset = source.get("y_offset").value();
            final NBTList entity = source.getList("source_entity");
            final int entityID;
            if (!source.containsKey("entity_id") || source.get("entity_id").tag() != NBT.Tag.INT)
                entityID = -1;
            else
                entityID = source.get("entity_id").value();

            if (entity.size() < 4) throw new NBTException();
            final ByteBuf buf = Unpooled.buffer();
            for (int i = 0; i < 4; i++)
                buf.writeInt(entity.get(i).value());
            final UUID uuid = new UUID(buf.readLong(), buf.readLong());

            return new EntityPositionSource(uuid, entityID, offset);
        }

        public EntityPositionSource(final Entity entity) {
            this(entity.getUuid(), entity.getEntityId(), (float) entity.getEntityType().getHeight());
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
        public static @Nullable PositionSourceType get(final NamespacedKey name) {
            for (final PositionSourceType type : PositionSourceType.values())
                if (type.name.equals(name)) return type;
            return null;
        }

    }

}
