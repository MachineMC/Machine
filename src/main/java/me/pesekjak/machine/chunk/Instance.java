package me.pesekjak.machine.chunk;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.NotNull;

@Getter
public class Instance {

    private final Machine server;
    private final World world;

    private long worldAge;

    private long time;
    private int timeRate = 1;

    public Instance(@NotNull World world) {
        if(world.manager() == null)
            throw new IllegalStateException("You can't create an instance of a world without a manager");
        this.world = world;
        this.server = world.manager().getServer();
    }

}
