package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day2")
public class Day2 implements Puzzle2025<Long, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day2.example.txt")
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
    public Long part1(Path input, boolean verbose) throws IOException {
        long sum = 0;
        List<Range> ranges = getRanges(input);
        if (verbose) {
            System.out.printf("%d Ranges were found:%n", ranges.size());
            ranges.forEach(r -> System.out.println(r.start + "-" + r.end));
        }

        for (Range range : ranges) {
            for (long i = range.start; i <= range.end; i++) {
                String number = String.valueOf(i);

                String repeatingBlock = "";
                while (repeatingBlock.length() < number.length()) {
                    repeatingBlock = number.substring(0, repeatingBlock.length() + 1);

                    boolean isRepeating = number.replaceFirst(repeatingBlock.repeat(2), "").isEmpty();
                    if (isRepeating && !repeatingBlock.equals(number)) {
                        if (verbose) {
                            System.out.printf("The number %20s consists of repeating blocks: %s%n", i, repeatingBlock);
                        }
                        sum += i;
                        break;
                    }
                }
            }
        }
        System.out.printf("The sum of the invalid IDs (repeating numbers) is %d%n", sum);
        return sum;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        long sum = 0;
        List<Range> ranges = getRanges(input);
        if (verbose) {
            System.out.printf("%d Ranges were found:%n", ranges.size());
            ranges.forEach(r -> System.out.println(r.start + "-" + r.end));
        }

        for (Range range : ranges) {
            for (long i = range.start; i <= range.end; i++) {
                String number = String.valueOf(i);

                String repeatingBlock = "";
                while (repeatingBlock.length() < number.length()) {
                    repeatingBlock = number.substring(0, repeatingBlock.length() + 1);

                    if (number.length() % repeatingBlock.length() == 0) {
                        int repeats = number.length() / repeatingBlock.length();
                        if (repeats >= 2 && repeatingBlock.repeat(repeats).equals(number)) {
                            if (verbose) {
                                System.out.printf("The number %20s consists of repeating blocks: %s%n", i, repeatingBlock);
                            }
                            sum += i;
                            break;
                        }
                    }
                }
            }
        }
        System.out.printf("The sum of the invalid IDs (repeating numbers) is %d%n", sum);
        return sum;
    }

    private List<Range> getRanges(Path inputFile) throws IOException {
        List<Range> ranges = new ArrayList<>();
        String input = String.join("", Files.readAllLines(inputFile));
        for (String rangeStr : input.split(",")) {
            String[] range = rangeStr.split("-");
            ranges.add(new Range(NumberUtils.toLong(range[0]), NumberUtils.toLong(range[1])));
        }
        return ranges;
    }

    private record Range(long start, long end) {}
}
