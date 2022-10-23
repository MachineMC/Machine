package me.pesekjak.machine.utils;

import java.util.concurrent.atomic.AtomicInteger;

public final class EntityUtils {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

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
