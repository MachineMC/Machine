package me.pesekjak.machine.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for entity operations.
 */
@UtilityClass
public class EntityUtils {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    /**
     * @return next empty entity id
     */
    public static int getEmptyID() {
        return ID_COUNTER.incrementAndGet();
    }

}
