package me.pesekjak.machine.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityUtils {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    public static int getEmptyID() {
        return ID_COUNTER.incrementAndGet();
    }

}
