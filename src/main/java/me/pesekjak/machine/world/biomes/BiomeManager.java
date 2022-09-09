package me.pesekjak.machine.world.biomes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.nbt.NBTSerializable;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class BiomeManager implements NBTSerializable, ServerProperty {

    private final List<Biome> biomes = new CopyOnWriteArrayList<>();
    @Getter
    private final Machine server;

    public static BiomeManager createDefault(Machine server) {
        BiomeManager manager = new BiomeManager(server);
        manager.addBiome(Biome.PLAINS);
        return manager;
    }

    public void addBiome(Biome biome) {
        if(!biomes.contains(biome))
            biomes.add(biome);
    }

    public boolean removeBiome(Biome biome) {
        return biomes.remove(biome);
    }

    public boolean isRegistered(NamespacedKey name) {
        return isRegistered(getBiome(name));
    }

    public boolean isRegistered(Biome biome) {
        return biomes.contains(biome);
    }


    public Biome getBiome(NamespacedKey name) {
        for(Biome biome : getBiomes()) {
            if(!(biome.getName().equals(name))) continue;
            return biome;
        }
        return null;
    }

    public List<Biome> getBiomes() {
        return Collections.unmodifiableList(biomes);
    }

    @Override
    public NBTCompound toNBT() {
        return NBT.Compound(Map.of(
                "type", NBT.String("minecraft:worldgen/biome"),
                "value", NBT.List(NBTType.TAG_Compound, biomes.stream().map(Biome::toNBT).toList())));
    }

}
