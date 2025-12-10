package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day10")
public class Day10 implements Puzzle2025<Integer, Integer> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day10.example.txt")
    private Path inputFile;

    @Option(names = {"-v", "--verbose"})
    private boolean verbose;

    @Override
    public Integer call() throws Exception {
        part1(inputFile, verbose);
        System.out.println();
        part2(inputFile, verbose);
        return 0;
    }

    @Override
    public Integer part1(Path input, boolean verbose) throws IOException {
        List<Machine> machines = parseInput(input);
        if (verbose) {
            System.out.println("The following machines have been parsed:");
            machines.forEach(System.out::println);
        }

        int totalMinPresses = 0;

        int machineCountForOutput = 1;
        for (Machine machine : machines) {
            int minPresses = calculateMinPresses(machine);

            if (verbose) {
                System.out.printf("Machine %d can be solved in %d presses%n", machineCountForOutput++, minPresses);
            }
            totalMinPresses += minPresses;
        }
        System.out.printf("The total number of presses required is %d%n", totalMinPresses);
        return totalMinPresses;
    }

    private static int calculateMinPresses(Machine machine) {
        int buttonSize = machine.buttons.size();
        int lightSize = machine.lights.size();

        int minPresses = Integer.MAX_VALUE;

        int totalCombinations = (int) Math.pow(2, buttonSize);
        for (int combination = 0; combination < totalCombinations; combination++) {
            List<Boolean> state = new ArrayList<>(Collections.nCopies(lightSize, false));
            int presses = 0;

            for (int buttonIndex = 0; buttonIndex < buttonSize; buttonIndex++) {
                boolean isButtonPressed = (combination & (1 << buttonIndex)) != 0;

                if (isButtonPressed) {
                    presses++;
                    for (int lightIdx : machine.buttons.get(buttonIndex).affectedLights()) {
                        state.set(lightIdx, !state.get(lightIdx));
                    }
                }
            }

            if (state.equals(machine.lights)) {
                minPresses = Math.min(minPresses, presses);
            }
        }
        return minPresses;
    }

    @Override
    public Integer part2(Path input, boolean verbose) throws IOException {
        System.out.println("Yeah, I just could not solve this one...");
        return -1;
    }

    private List<Machine> parseInput(Path input) throws IOException {
        List<Machine> machines = new ArrayList<>();

        for (String line : Files.readAllLines(input)) {
            Machine machine = new Machine();

            Matcher lightsMatcher = java.util.regex.Pattern.compile("\\[([.#]+)]").matcher(line);
            if (lightsMatcher.find()) {
                String lights = lightsMatcher.group(1);
                for (char c : lights.toCharArray()) {
                    machine.lights.add(c == '#');
                }
            }

            Matcher buttonsMatcher = java.util.regex.Pattern.compile("\\(([0-9,]+)\\)").matcher(line);
            while (buttonsMatcher.find()) {
                String buttonStr = buttonsMatcher.group(1);
                List<Integer> affectedLights = new ArrayList<>();
                for (String num : buttonStr.split(",")) {
                    affectedLights.add(NumberUtils.toInt(num));
                }
                machine.buttons.add(new Button(affectedLights));
            }

            Matcher joltagesMatcher = java.util.regex.Pattern.compile("\\{([0-9,]+)}").matcher(line);
            if (joltagesMatcher.find()) {
                String joltagesStr = joltagesMatcher.group(1);
                for (String num : joltagesStr.split(",")) {
                    machine.joltages.add(Long.parseLong(num));
                }
            }
            machines.add(machine);
        }
        return machines;
    }

    public record Machine(
        List<Boolean> lights,
        List<Button> buttons,
        List<Long> joltages
    ) {
        public Machine() {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    public record Button(List<Integer> affectedLights) {}
}
