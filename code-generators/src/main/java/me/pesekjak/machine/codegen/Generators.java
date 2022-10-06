package me.pesekjak.machine.codegen;

import me.pesekjak.machine.codegen.blockdata.BlockDataLibGenerator;
import me.pesekjak.machine.codegen.materials.MaterialsLibGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Generators {

    public static final String OUTPUT_PATH = "../../../libs/";

    public static void main(String[] args) {
        try {
            if(!new File("../../../../Machine/").exists()) {
                System.out.println("Machine Library Generator has to run as jar inside of libs folder and Machine project");
                System.exit(0);
            }
            boolean s = new File(OUTPUT_PATH).mkdirs();
            new Generators();
        } catch (Exception exception) {
            System.out.println("Machine Library Generator unexpectedly ended.");
            exception.printStackTrace();
        }
    }

    public Generators() throws Exception {
        System.out.println("Machine Library Generator");
        System.out.println("0 - CANCEL");
        System.out.println("1 - Materials Library");
        System.out.println("2 - BlockData Library");
        System.out.println("3 - ALL");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int next = Integer.parseInt(reader.readLine());
        switch (next) {
            case 0 -> System.exit(0);
            case 1 -> materials();
            case 2 -> blockdata();
            case 3 -> {
                materials();
                blockdata();
            }
        }
    }

    public void materials() throws IOException {
        new MaterialsLibGenerator().generate();
    }

    public void blockdata() throws IOException {
        new BlockDataLibGenerator().generate();
    }

}
