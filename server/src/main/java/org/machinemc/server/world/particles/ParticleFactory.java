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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.world.particles;

import org.machinemc.server.utils.ClassUtils;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.world.particles.options.BlockOptionsImpl;
import org.machinemc.api.world.particles.Particle;
import org.machinemc.api.world.particles.ParticleOptions;
import org.machinemc.api.world.particles.ParticleType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating particles from data or their default variations.
 */
public final class ParticleFactory {

    private static final Map<ParticleType, ParticleCreator> CREATOR_MAP = new HashMap<>();
    private static final Map<
            Class<? extends ParticleOptions>,
            Supplier<? extends ParticleOptions>
            > DEFAULT_OPTIONS_MAP = new HashMap<>();

    static {
        CREATOR_MAP.put(ParticleType.AMBIENT_ENTITY_EFFECT, ParticleCreator.EMPTY);
        CREATOR_MAP.put(ParticleType.ANGRY_VILLAGER, ParticleCreator.EMPTY);
        CREATOR_MAP.put(ParticleType.BLOCK, (type, buf) -> ParticleImpl.of(type, new BlockOptionsImpl(buf)));
        try {
            ClassUtils.loadClasses(ParticleFactory.class.getPackageName());
        } catch (IOException ignored) { }
    }

    private ParticleFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers new particle options type to the factory.
     * @param optionsClass interface of the options class
     * @param supplier supplier of the default options variant
     * @param <T> options
     * @throws UnsupportedOperationException if the options class isn't interface
     */
    public static <T extends ParticleOptions> void registerOption(final Class<T> optionsClass,
                                                                  final Supplier<T> supplier) {
        if (!optionsClass.isInterface())
            throw new UnsupportedOperationException();
        DEFAULT_OPTIONS_MAP.put(optionsClass, supplier);
    }

    /**
     * Creates a particle with given type and options from the data of the given
     * buffer.
     * @param type type of the particle
     * @param buf particle options
     * @return new particle
     */
    public static Particle create(final ParticleType type, final ServerBuffer buf) {
        return CREATOR_MAP.get(type).create(type, buf);
    }

    /**
     * Creates a particle with its default options.
     * @param type type of the particle
     * @return new particle
     */
    public static Particle create(final ParticleType type) {
        return ParticleImpl.of(type, DEFAULT_OPTIONS_MAP.get(type.getOptions()).get());
    }

}
