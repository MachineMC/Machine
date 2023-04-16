package org.machinemc.api.utils;

/**
 * Pair of key and value.
 * @param <F> type of first value
 * @param <S> type of second value
 * @param first first value
 * @param second second value
 */
public record Pair<F, S>(F first, S second) {

}
