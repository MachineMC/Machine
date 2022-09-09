package me.pesekjak.machine.utils;

import lombok.Data;

@SuppressWarnings("ClassCanBeRecord")
@Data
public class Pair<F, S> {

    private final F first;
    private final S second;

}
