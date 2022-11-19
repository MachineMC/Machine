package me.pesekjak.machine.utils;

@FunctionalInterface
public interface Writable {

    void write(FriendlyByteBuf buf);

}
