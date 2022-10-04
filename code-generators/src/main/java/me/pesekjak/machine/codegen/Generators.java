package me.pesekjak.machine.codegen;

import me.pesekjak.machine.codegen.materials.MaterialsLibGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Generators {

    public static final String OUTPUT_PATH = "../../../libs/";

    public static void main(String[] args) {
        try {
            if(!new File(OUTPUT_PATH).exists()) {
                System.out.println("Machine Library Generator has to run as jar inside of libs folder and Machine project");
                System.exit(0);
            }
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int next = Integer.parseInt(reader.readLine());
        switch (next) {
            case 0 -> System.exit(0);
            case 1 -> materials();
        }
    }

    public void materials() throws IOException {
        new MaterialsLibGenerator().generate();
    }

}
