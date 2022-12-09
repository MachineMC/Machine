package me.pesekjak.machine.world.particles;

import lombok.experimental.UtilityClass;
import me.pesekjak.machine.utils.ClassUtils;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.particles.options.BlockOptionsImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating particles from data or their default variations.
 */
@UtilityClass
public class ParticleFactory {

    private final Map<ParticleType, ParticleCreator> CREATOR_MAP = new HashMap<>();
    private final Map<Class<? extends ParticleOptions>, Supplier<? extends ParticleOptions>> DEFAULT_OPTIONS_MAP = new HashMap<>();

    static {
        CREATOR_MAP.put(ParticleType.AMBIENT_ENTITY_EFFECT, ParticleCreator.empty);
        CREATOR_MAP.put(ParticleType.ANGRY_VILLAGER, ParticleCreator.empty);
        CREATOR_MAP.put(ParticleType.BLOCK, ((type, buf) -> ParticleImpl.of(type, new BlockOptionsImpl(buf))));
        try {
            ClassUtils.loadClasses(ParticleFactory.class.getPackageName());
        } catch (IOException ignored) { }
    }

    /**
     * Registers new particle options type to the factory.
     * @param optionsClass interface of the options class
     * @param supplier supplier of the default options variant
     * @param <T> options
     * @throws UnsupportedOperationException if the options class isn't interface
     */
    public static <T extends ParticleOptions> void registerOption(@NotNull Class<T> optionsClass, @NotNull Supplier<T> supplier) {
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
    public static @NotNull ParticleImpl create(@NotNull ParticleType type, @NotNull ServerBuffer buf) {
        return CREATOR_MAP.get(type).create(type, buf);
    }

    /**
     * Creates a particle with its default options.
     * @param type type of the particle
     * @return new particle
     */
    public static @NotNull ParticleImpl create(@NotNull ParticleType type) {
        return ParticleImpl.of(type, DEFAULT_OPTIONS_MAP.get(type.getOptions()).get());
    }

}
