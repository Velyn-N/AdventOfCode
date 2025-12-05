package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day5")
public class Day5 implements Puzzle2025<Integer, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day5.example.txt")
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
        IngredientData data = getIngredientData(input);
        long freshCount = data.ingredients
                .stream()
                .filter(i -> data.freshIngredients
                        .stream()
                        .anyMatch(r -> r.start <= i && i <= r.end))
                .count();
        System.out.printf("The inventory contains %d fresh out of %d total ingredients%n", freshCount, data.ingredients.size());
        return (int) freshCount;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        IngredientData data = getIngredientData(input);
        if (data.freshIngredients.isEmpty()) {
            System.out.println("No fresh ingredients were found, exiting...");
            return 0L;
        }

        List<Range> sortedRanges = data.freshIngredients.stream().sorted().toList();
        List<Range> combinedRanges = combineRanges(sortedRanges);

        long freshIdAmount = 0L;
        for (Range range : combinedRanges) {
            freshIdAmount += (range.end - range.start + 1);
        }
        System.out.printf("In total the inventory contains %d fresh ingredients%n", freshIdAmount);
        return freshIdAmount;
    }

    private List<Range> combineRanges(List<Range> sortedRanges) {
        List<Range> combinedRanges = new ArrayList<>();
        Range current = sortedRanges.getFirst();

        for (int i = 1; i < sortedRanges.size(); i++) {
            Range next = sortedRanges.get(i);

            if (next.start <= current.end + 1) {
                long newEnd = Math.max(current.end, next.end);
                current = new Range(current.start, newEnd);
            } else {
                combinedRanges.add(current);
                current = next;
            }
        }
        combinedRanges.add(current);
        return combinedRanges;
    }

    private IngredientData getIngredientData(Path input) throws IOException {
        List<Range> freshIngredients = new ArrayList<>();
        List<Long> ingredients = new ArrayList<>();
        boolean isAfterSpacer = false;
        for (String line : Files.readAllLines(input)) {
            if (line.isBlank()) {
                isAfterSpacer = true;
                continue;
            }
            if (isAfterSpacer) {
                ingredients.add(NumberUtils.toLong(line));
            } else {
                String[] rangeDelimiters = line.split("-");
                long start = NumberUtils.toLong(rangeDelimiters[0]);
                long end = NumberUtils.toLong(rangeDelimiters[1]);
                freshIngredients.add(new Range(start, end));
            }
        }
        return new IngredientData(freshIngredients, ingredients);
    }

    public record IngredientData(List<Range> freshIngredients, List<Long> ingredients) {}

    public record Range(Long start, Long end) implements Comparable<Range> {

        @Override
        public int compareTo(Range range) {
            if (start < range.start) {
                return -1;
            }
            if (start > range.start) {
                return 1;
            }
            return end.compareTo(range.end);
        }
    }
}
