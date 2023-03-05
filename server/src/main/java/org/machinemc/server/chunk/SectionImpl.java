package org.machinemc.server.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.machinemc.api.chunk.Section;
import org.machinemc.server.chunk.palette.AdaptivePalette;
import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.ServerBuffer;

/**
 * Default implementation of the section.
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

    @Override
    public void clear() {
        this.blockPalette.fill(0);
        this.biomePalette.fill(0);
        this.skyLight = new byte[0];
        this.blockLight = new byte[0];
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public SectionImpl clone() {
        return new SectionImpl(blockPalette.clone(), biomePalette.clone(), skyLight.clone(), blockLight.clone());
    }

    @Override
    public void write(ServerBuffer buf) {
        buf.writeShort((short) blockPalette.count());
        blockPalette.write(buf);
        biomePalette.write(buf);
    }

}
