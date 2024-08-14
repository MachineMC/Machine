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
package org.machinemc.server;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TickerTest {

    /**
     * Run next tick.
     */
    @Test
    public void testTicker() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        assert ticker.runNextTick(() -> 1).get(1, TimeUnit.SECONDS).equals(1);
        ticker.close();
    }

    /**
     * Tests whether the ticker runs the tasks on the correct thread.
     */
    @Test
    public void testTickerThread() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        assert ticker.runNextTick(() -> ticker.isTickThread()).get();
        ticker.close();
    }

    /**
     * Tests freezing of the ticker.
     */
    @Test
    public void testFreeze() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        ticker.freeze().get();
        assert ticker.isFrozen();
        assertThrows(TimeoutException.class, () -> ticker.runNextTick(() -> 1).get(1, TimeUnit.SECONDS));
        ticker.unfreeze();
        ticker.close();
    }

    /**
     * Tests unfreezing of the ticker.
     */
    @Test
    public void testUnfreeze() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        ticker.freeze().get();
        final CompletableFuture<Integer> future = ticker.runNextTick(() -> 1);
        assert ticker.isFrozen();
        assert ticker.unfreeze();
        assert !ticker.isFrozen();
        assert future.get(1, TimeUnit.SECONDS) == 1;
        ticker.close();
    }

    /**
     * Tests the order of running tasks.
     */
    @Test
    public void testOrder() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        final List<Integer> numbers = new CopyOnWriteArrayList<>();
        ticker.runAfter(() -> numbers.add(1), 1);
        ticker.runAfter(() -> numbers.add(2), 1);
        ticker.runAfter(() -> numbers.add(3), 2);
        ticker.runAfter(() -> numbers.add(4), 3);
        ticker.runAfter(() -> numbers.add(5), 3).get();
        assert numbers.equals(List.of(1, 2, 3, 4, 5));
        ticker.close();
    }

    /**
     * Test for stepping ticks forward.
     */
    @Test
    public void stepTicks() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        ticker.freeze().get();
        final AtomicBoolean condition = new AtomicBoolean(false);
        for (int i = 0; i < 5; i++) {
            ticker.runAfter(() -> condition.set(true), i);
        }

        for (int i = 0; i < 5; i++) {
            ticker.step().get();
            assert condition.get();
            condition.set(false);
        }

        assert ticker.unfreeze();

        ticker.close();
    }

    /**
     * Test for calculating TPS.
     */
    @Test
    public void testTPS() throws Exception {
        final Ticker ticker = new TickerImpl(Thread.ofVirtual(), 20);
        ticker.runNextTick(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });

        Thread.sleep(1000);

        final float tickRate = ticker.getTickRate();
        assert 18 <= tickRate && tickRate <= 19; // one tick took 100ms instead of 50, the tick rate should be between 18 and 18

        Thread.sleep(1000);

        assert ticker.getTickRate() == 20; // one second later it should be back to 20

        ticker.close();
    }

}
