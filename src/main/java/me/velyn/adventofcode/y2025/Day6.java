package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day6")
public class Day6 implements Puzzle2025<Long, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day6.example.txt")
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
        List<Calculation> calculations = parseCalculations(input);
        long totalSum = performCalculations(verbose, calculations);
        System.out.printf("The total sum of the calculations is %d%n", totalSum);
        return totalSum;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        List<Calculation> calculations = parseCalculationsPart2(input);

        long totalSum = performCalculations(verbose, calculations);
        System.out.printf("The total sum of the calculations is %d%n", totalSum);
        return totalSum;
    }

    private long performCalculations(boolean verbose, List<Calculation> calculations) {
        if (verbose) {
            System.out.println("Calculations:");
            calculations.forEach(System.out::println);
            System.out.println("Results:");
        }
        long totalSum = 0;
        for (Calculation calculation : calculations) {
            long result = 0;
            if ("+".equals(calculation.operation)) {
                result = calculation.numbers.stream().mapToLong(l -> l).reduce(0L, Long::sum);
            } else if ("*".equals(calculation.operation)) {
                result = calculation.numbers.stream().mapToLong(l -> l).reduce(1L, Math::multiplyExact);
            }
            totalSum += result;
            if (verbose) {
                System.out.printf("%s = %d (total = %d)%n", calculation, result, totalSum);
            }
        }
        return totalSum;
    }

    private List<Calculation> parseCalculations(Path input) throws IOException {
        Map<Integer, Calculation> calculations = new HashMap<>();
        for (String line : Files.readAllLines(input)) {
            String[] split = line.split(" ");

            int col = 0;
            for (String number : split) {
                if (StringUtils.isBlank(number)) {
                    continue;
                }
                if (StringUtils.isNumeric(number)) {
                    calculations.computeIfAbsent(col, k -> new Calculation()).numbers.add(Long.parseLong(number));
                } else {
                    calculations.computeIfAbsent(col, k -> new Calculation()).operation = number;
                }
                col++;
            }
        }
        return new ArrayList<>(calculations.values());
    }

    private List<Calculation> parseCalculationsPart2(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input);
        int maxWidth = lines.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        List<Calculation> calculations = new ArrayList<>();

        Calculation currentCalc = new Calculation();
        boolean isNewCalc = true;

        for (int col = maxWidth - 1; col >= 0; col--) {
            StringBuilder numberStr = new StringBuilder();
            String operator = null;
            boolean hasDigit = false;

            for (String line : lines) {
                if (col >= line.length()) {
                    continue;
                }

                char c = line.charAt(col);
                if (Character.isDigit(c)) {
                    numberStr.append(c);
                    hasDigit = true;
                } else if (c == '+' || c == '*') {
                    operator = String.valueOf(c);
                }
            }

            if (!hasDigit && operator == null) {
                if (!isNewCalc && !currentCalc.numbers.isEmpty()) {
                    calculations.add(currentCalc);
                    currentCalc = new Calculation();
                    isNewCalc = true;
                }
                continue;
            }

            if (hasDigit) {
                currentCalc.numbers.add(Long.parseLong(numberStr.toString()));
                isNewCalc = false;
            }

            if (operator != null) {
                currentCalc.operation = operator;
            }
        }

        if (!currentCalc.numbers.isEmpty()) {
            calculations.add(currentCalc);
        }

        return calculations;
    }

    public static class Calculation {
        String operation;
        List<Long> numbers = new ArrayList<>();

        @Override
        public String toString() {
            return numbers.stream().map(String::valueOf).collect(Collectors.joining(" " + operation + " "));
        }
    }
}
