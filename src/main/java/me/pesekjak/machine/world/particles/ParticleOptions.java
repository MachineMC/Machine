package me.pesekjak.machine.world.particles;

import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.FriendlyByteBuf;

/**
 * Options for the ParticleTypes
 */
public interface ParticleOptions extends NBTSerializable {

    /**
     * Writes the particle option into a buffer, particle ID excluded
     * @param buf buffer to write into
     * @return the same buffer
     */
    FriendlyByteBuf write(FriendlyByteBuf buf);

}
