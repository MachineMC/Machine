package org.machinemc.api.world.particles;

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.Writable;

/**
 * Represents an option for the particle type.
 */
public interface ParticleOptions extends NBTSerializable, Writable, Cloneable {

    ParticleOptions clone();

}
