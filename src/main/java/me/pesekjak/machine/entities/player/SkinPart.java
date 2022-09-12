package me.pesekjak.machine.entities.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public enum SkinPart {

    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40);

    @Getter
    private final int mask;

    public static int skinMask(SkinPart... parts) {
        int mask = 0;
        for(SkinPart part : parts)
            mask |= part.mask;
        return mask;
    }

    public static Set<SkinPart> fromMask(int mask) {
        Set<SkinPart> set = new HashSet<>();
        for (SkinPart skinPart : values()) {
            if ((skinPart.mask & mask) == skinPart.mask) set.add(skinPart);
            if (skinPart.mask > mask) return set;
        }
        return set;
    }

}
