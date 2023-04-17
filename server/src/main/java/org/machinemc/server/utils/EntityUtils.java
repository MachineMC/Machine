package org.machinemc.server.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for entity operations.
 */
public final class EntityUtils {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private EntityUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return next empty entity id
     */
    public static int getEmptyID() {
        return ID_COUNTER.incrementAndGet();
    }

}
