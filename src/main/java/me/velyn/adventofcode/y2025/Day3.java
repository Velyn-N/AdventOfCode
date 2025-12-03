package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;

import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day1")
public class Day3 implements Puzzle2025<Integer, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day3.example.txt")
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
        int totalCapacity = 0;
        for (String line : Files.readAllLines(input)) {

            int highestCapacity = 0;

            for (int i = 0; i < line.length() - 1; i++) {
                for (int j = i + 1; j < line.length(); j++) {
                    var c1 = String.valueOf(line.charAt(i));
                    var c2 = String.valueOf(line.charAt(j));

                    int capacity = NumberUtils.toInt(c1 + c2);

                    if (capacity > highestCapacity && i != j) {
                        highestCapacity = capacity;
                    }
                }
            }
            if (verbose) {
                System.out.printf("The highest capacity of this line is %3s%n", highestCapacity);
            }
            totalCapacity += highestCapacity;
        }
        System.out.printf("The total capacity of all battery banks is %d jolts%n", totalCapacity);
        return totalCapacity;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        long totalCapacity = 0;
        for (String line : Files.readAllLines(input)) {
            if (line.length() < 12) {
                continue;
            }

            String string = getHighestPossibleNumber(line);

            long highestCapacity = Long.parseLong(string);

            if (verbose) {
                System.out.printf("The highest capacity of this line is %d%n", highestCapacity);
            }
            totalCapacity += highestCapacity;
        }
        System.out.printf("The total capacity of all battery banks is %d jolts%n", totalCapacity);
        return totalCapacity;
    }

    private String getHighestPossibleNumber(String line) {
        StringBuilder sb = new StringBuilder();
        int searchingFrom = 0;

        for (int needed = 12; needed > 0; needed--) {
            int highestPossibleSearchIndex = line.length() - needed;

            char biggestDigit = 0;
            int indexOfBiggestDigit = -1;

            for (int i = searchingFrom; i <= highestPossibleSearchIndex; i++) {
                char c = line.charAt(i);

                if (c > biggestDigit) {
                    biggestDigit = c;
                    indexOfBiggestDigit = i;
                    if (c == '9') {
                        break;
                    }
                }
            }

            sb.append(biggestDigit);
            searchingFrom = indexOfBiggestDigit + 1;
        }
        return sb.toString();
    }
}
