package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;

import picocli.CommandLine.*;

@Command(name = "day11")
public class Day11 implements Puzzle2025<Integer, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day11.example.txt")
    private Path inputFile;

    @Option(names = {"-v", "--verbose"})
    private boolean verbose;

    @Option(names = {"-p", "--part"})
    private int part = 1;

    @Override
    public Integer call() throws Exception {
        if (part == 1) {
            part1(inputFile, verbose);
        } else if (part == 2) {
            part2(inputFile, verbose);
        } else {
            System.out.println("Please specify a part for this command as they might need different inputs");
            return -1;
        }
        return 0;
    }

    @Override
    public Integer part1(Path input, boolean verbose) throws IOException {
        List<Device> devices = parseInput(input);

        var startingPoint = devices.stream().filter(d -> "you".equals(d.id)).findAny();
        if (startingPoint.isEmpty()) {
            System.out.println("No device called 'you', so we are missing our starting point");
            return -1;
        }

        int routesToReactor = followTargetsPart1(devices, startingPoint.get());
        System.out.printf("There are %d routes to the reactor%n", routesToReactor);
        return routesToReactor;
    }

    private int followTargetsPart1(List<Device> devices, Device startingPoint) {
        int count = 0;

        for (String target : startingPoint.targets) {
            if ("out".equals(target)) {
                count++;
                continue;
            }
            var optTarget = devices.stream()
                    .filter(d -> Strings.CS.equals(d.id, target))
                    .findAny();

            if (optTarget.isEmpty()) {
                System.out.printf("Could not find device '%s'%n", target);
                continue;
            }
            count += followTargetsPart1(devices, optTarget.get());
        }
        return count;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        Map<String, Device> devices = parseInput(input).stream().collect(Collectors.toMap(d -> d.id, d -> d));

        if (!devices.containsKey("svr")) {
            System.out.println("No device called 'svr', so we are missing our starting point");
            return -1L;
        }

        Map<TreeTraversalState, Long> memo = new HashMap<>();
        long routesToReactor = followTargetsPart2(devices, devices.get("svr"), false, false, memo);
        System.out.printf("There are %d routes to the reactor that also meet at least one 'fft' and 'dac' device%n", routesToReactor);
        return routesToReactor;
    }

    private long followTargetsPart2(Map<String, Device> devices,
                                    Device current,
                                    boolean hasMetFFT,
                                    boolean hasMetDAC,
                                    Map<TreeTraversalState, Long> memo) {

        if ("fft".equals(current.id)) {
            hasMetFFT = true;
        }
        if ("dac".equals(current.id)) {
            hasMetDAC = true;
        }

        TreeTraversalState key = new TreeTraversalState(current.id, hasMetFFT, hasMetDAC);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        long count = 0;

        for (String target : current.targets) {
            if ("out".equals(target)) {
                if (hasMetFFT && hasMetDAC) {
                    count++;
                }
                continue;
            }

            Device next = devices.get(target);
            if (next == null) {
                System.out.printf("Could not find device '%s'%n", target);
                continue;
            }

            count += followTargetsPart2(devices, next, hasMetFFT, hasMetDAC, memo);
        }

        memo.put(key, count);
        return count;
    }

    private List<Device> parseInput(Path input) throws IOException {
        List<Device> devices = new ArrayList<>();
        for (String line : Files.readAllLines(input)) {
            String[] words = line.split(" ");
            if (words.length < 2) {
                continue;
            }
            String deviceId = words[0].replace(":", "");
            List<String> targets = Arrays.asList(words).subList(1, words.length);
            devices.add(new Device(deviceId, targets));
        }
        return devices;
    }

    private record TreeTraversalState(String deviceId, boolean hasFFT, boolean hasDAC) {}

    public record Device(String id, List<String> targets) {}
}
