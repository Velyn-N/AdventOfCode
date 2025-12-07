package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import picocli.CommandLine.*;

@Command(name = "day7")
public class Day7 implements Puzzle2025<Integer, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day7.example.txt")
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
        List<List<InputElement>> inputElements = parseInput(input);
        int splitCounter = 0;

        for (int rowI = 0; rowI < inputElements.size() - 1; rowI++) {
            List<InputElement> row = inputElements.get(rowI);
            List<InputElement> nextRow = inputElements.get(rowI + 1);

            for (int colI = 0; colI < row.size(); colI++) {
                switch (row.get(colI)) {
                    case SOURCE, BEAM -> {
                        if (nextRow.get(colI) == InputElement.EMPTY) {
                            nextRow.set(colI, InputElement.BEAM);
                        }
                    }
                    case SPLITTER -> {
                        if (rowI == 0) {
                            break;
                        }
                        List<InputElement> prevRow = inputElements.get(rowI - 1);
                        if (prevRow.get(colI) == InputElement.BEAM) {
                            splitCounter++;
                            if (colI > 0) {
                                nextRow.set(colI - 1, InputElement.BEAM);
                            }
                            if (colI + 1 < nextRow.size()) {
                                nextRow.set(colI + 1, InputElement.BEAM);
                            }
                        }
                    }
                }
            }
        }
        if (verbose) {
            inputElements.forEach(row -> {
                row.forEach(e -> System.out.print(e.c));
                System.out.println();
            });
        }
        System.out.printf("The beam has been split a total of %d times%n", splitCounter);
        return splitCounter;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        List<List<InputElement>> inputElements = parseInput(input);
        Map<Integer, Long> activeTimelines = new HashMap<>();

        for (int colI = 0; colI < inputElements.getFirst().size(); colI++) {
            if (inputElements.getFirst().get(colI) == InputElement.SOURCE) {
                activeTimelines.put(colI, 1L);
                break;
            }
        }

        for (int rowI = 0; rowI < inputElements.size() - 1; rowI++) {
            List<InputElement> nextRow = inputElements.get(rowI + 1);
            Map<Integer, Long> nextTimelines = new HashMap<>();

            for (Map.Entry<Integer, Long> entry : activeTimelines.entrySet()) {
                int col = entry.getKey();
                long count = entry.getValue();

                InputElement target = nextRow.get(col);

                if (target == InputElement.SPLITTER) {
                    if (col - 1 >= 0) {
                        nextTimelines.merge(col - 1, count, Long::sum);
                    }
                    if (col + 1 < nextRow.size()) {
                        nextTimelines.merge(col + 1, count, Long::sum);
                    }
                } else {
                    nextTimelines.merge(col, count, Long::sum);
                }
            }
            activeTimelines = nextTimelines;
        }
        return activeTimelines.values().stream().mapToLong(Long::longValue).sum();
    }

    private List<List<InputElement>> parseInput(Path input) throws IOException {
        List<List<InputElement>> inputElements = new ArrayList<>();
        for (String line : Files.readAllLines(input)) {
            List<InputElement> elements = new ArrayList<>();
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'S' -> elements.add(InputElement.SOURCE);
                    case '|' -> elements.add(InputElement.BEAM);
                    case '^' -> elements.add(InputElement.SPLITTER);
                    default -> elements.add(InputElement.EMPTY);
                }
            }
            inputElements.add(elements);
        }
        return inputElements;
    }

    public enum InputElement {
        EMPTY('*'),
        SOURCE('S'),
        SPLITTER('^'),
        BEAM('|');

        public final char c;

        InputElement(char c) {
            this.c = c;
        }
    }
}
