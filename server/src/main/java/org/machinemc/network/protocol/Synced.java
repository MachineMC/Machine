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
package org.machinemc.network.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate packet types that should be handled on the tick thread.
 * <p>
 * By default, packet listening methods of extensions of {@link PacketListener} do
 * not run on a tick thread. If a packet class is annotated with this annotation,
 * then the listening code will always run on a main server thread.
 *
 * @see org.machinemc.server.Ticker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Synced {

    /**
     * This annotation can easily disrupt the order of received packets.
     * <p>
     * The new incoming packets annotated with {@link Synced} are always
     * handled the next server tick, the packets that are not
     * annotated are handled immediately by one of the netty threads.
     * <p>
     * This method addresses the issue and can block the packet reading
     * until the synced packet is handled by the main thread. This preserves
     * the order of incoming packets but may delay all other incoming packets.
     *
     * @return whether the synced packet should block channel reading until it
     * is handled by the main thread.
     */
    boolean blocks() default false;

    // TODO later we implement World class, the synced annotation should execute the
    //  listener on the world ticker, not server ticker if there is world available

}
