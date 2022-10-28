package me.pesekjak.machine.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.chunk.palette.Palette;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class Section implements Cloneable {

    private final Palette blockPalette;
    private final Palette biomePalette;
    @Setter
    private byte[] skyLight;
    @Setter
    private byte[] blockLight;

    public Section() {
        this(Palette.blocks(), Palette.biomes(),
                new byte[0], new byte[0]);
    }

    public void clear() {
        this.blockPalette.fill(0);
        this.biomePalette.fill(0);
        this.skyLight = new byte[0];
        this.blockLight = new byte[0];
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @NotNull Section clone() {
        return new Section(blockPalette.clone(), biomePalette.clone(), skyLight.clone(), blockLight.clone());
    }

    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeShort((short) blockPalette.count());
        blockPalette.write(buf);
        biomePalette.write(buf);
    }

}
