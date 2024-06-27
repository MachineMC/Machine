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

import com.google.common.base.Preconditions;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * A TemporalUnit that represents the length of server tick in perfect conditions.
 * <p>
 * The game runs at a fixed tick rate of 20 ticks per second (TPS),
 * meaning each tick occurs every 50 milliseconds.
 * <p>
 * This class is not for measuring the length that a tick
 * took, rather it is used for simple conversion between times and ticks.
 */
public final class Tick implements TemporalUnit {

    /**
     * Milliseconds in one tick in perfect conditions.
     */
    public static int TICK_MILLIS = 1000 / 20;

    private static final Tick INSTANCE = new Tick(TICK_MILLIS);

    private final long millis;

    private Tick(final long millis) {
        Preconditions.checkState(millis > 0, "Illegal value");
        this.millis = millis;
    }

    /**
     * Returns the tick instance.
     *
     * @return tick instance
     */
    public static Tick get() {
        return INSTANCE;
    }

    /**
     * Creates a duration from an amount of ticks.
     *
     * @param ticks number of ticks
     * @return duration
     */
    public static Duration of(long ticks) {
        return Duration.of(ticks, INSTANCE);
    }

    /**
     * Returns number of ticks in a duration.
     *
     * @param value duration
     * @param timeUnit unit
     * @return number of ticks
     */
    public static long of(long value, TimeUnit timeUnit) {
        return of(value, timeUnit.toChronoUnit());
    }

    /**
     * Returns number of ticks in a duration.
     *
     * @param value duration
     * @param chronoUnit unit
     * @return number of ticks
     */
    public static long of(long value, ChronoUnit chronoUnit) {
        return of(Duration.of(value, chronoUnit));
    }

    /**
     * Returns number of ticks in a duration.
     *
     * @param duration duration
     * @return number of ticks
     */
    public static long of(Duration duration) {
        return duration.toMillis() / TICK_MILLIS;
    }

    /**
     * Gets the number of whole ticks that occur in the provided duration.
     *
     * @param duration the duration
     * @return the number of whole ticks in this duration
     * @throws ArithmeticException if the duration is zero or an overflow occurs
     */
    public int fromDuration(Duration duration) {
        Preconditions.checkNotNull(duration, "Duration can not be null");
        return Math.toIntExact(Math.floorDiv(duration.toMillis(), millis));
    }

    @Override
    public Duration getDuration() {
        return Duration.ofMillis(millis);
    }

    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    @Override
    public boolean isDateBased() {
        return false;
    }

    @Override
    public boolean isTimeBased() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) temporal.plus(getDuration().multipliedBy(amount));
    }

    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, ChronoUnit.MILLIS) / millis;
    }

}
