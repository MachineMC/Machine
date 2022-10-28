package me.pesekjak.machine.utils;

public class MathUtils {

    /**
     * @param n number to check
     * @return by how many bits the number can be represented
     */
    public static int bitsToRepresent(int n) {
        if(n < 1) throw new IllegalStateException("Number must be greater than 0");
        return Integer.SIZE - Integer.numberOfLeadingZeros(n);
    }

}
