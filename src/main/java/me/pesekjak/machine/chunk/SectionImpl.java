package me.pesekjak.machine.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.chunk.palette.AdaptivePalette;
import me.pesekjak.machine.chunk.palette.Palette;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * 16*16*16 sections of a chunk.
 */
@AllArgsConstructor
@Getter
public class SectionImpl implements Section {

    private final Palette blockPalette;
    private final Palette biomePalette;
    @Setter
    private byte[] skyLight;
    @Setter
    private byte[] blockLight;

    public SectionImpl() {
        this(AdaptivePalette.blocks(), AdaptivePalette.biomes(),
                new byte[0], new byte[0]);
    }

    /**
     * Clears all data in the section.
     */
    public void clear() {
        this.blockPalette.fill(0);
        this.biomePalette.fill(0);
        this.skyLight = new byte[0];
        this.blockLight = new byte[0];
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @NotNull SectionImpl clone() {
        return new SectionImpl(blockPalette.clone(), biomePalette.clone(), skyLight.clone(), blockLight.clone());
    }

    /**
     * Writes the section to a given buffer.
     * @param buf buffer to write into
     */
    public void write(@NotNull ServerBuffer buf) {
        buf.writeShort((short) blockPalette.count());
        blockPalette.write(buf);
        biomePalette.write(buf);
    }

}
