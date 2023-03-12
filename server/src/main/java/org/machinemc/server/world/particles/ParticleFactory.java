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
    private static final Map<Class<? extends ParticleOptions>, Supplier<? extends ParticleOptions>> DEFAULT_OPTIONS_MAP = new HashMap<>();

    static {
        CREATOR_MAP.put(ParticleType.AMBIENT_ENTITY_EFFECT, ParticleCreator.empty);
        CREATOR_MAP.put(ParticleType.ANGRY_VILLAGER, ParticleCreator.empty);
        CREATOR_MAP.put(ParticleType.BLOCK, ((type, buf) -> ParticleImpl.of(type, new BlockOptionsImpl(buf))));
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
    public static <T extends ParticleOptions> void registerOption(Class<T> optionsClass, Supplier<T> supplier) {
        if(!optionsClass.isInterface())
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
    public static Particle create(ParticleType type, ServerBuffer buf) {
        return CREATOR_MAP.get(type).create(type, buf);
    }

    /**
     * Creates a particle with its default options.
     * @param type type of the particle
     * @return new particle
     */
    public static Particle create(ParticleType type) {
        return ParticleImpl.of(type, DEFAULT_OPTIONS_MAP.get(type.getOptions()).get());
    }

}