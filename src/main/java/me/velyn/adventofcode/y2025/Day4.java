package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import lombok.*;
import picocli.CommandLine.*;

@Command(name = "day4")
public class Day4 implements Puzzle2025<Integer, Integer> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day4.example.txt")
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
        int accessibleRollCount = 0;
        Grid<Boolean> grid = getPaperRollGrid(input);
        for (int i = 0; i < grid.countRows(); i++) {
            for (int j = 0; j < grid.countCols(i); j++) {
                Boolean isRoll = grid.get(i, j);
                if (isRoll == null || !isRoll) {
                    continue;
                }
                List<Boolean> surrounding = grid.getSurrounding(i, j);
                if (surrounding.stream().filter(b -> b).count() < 4) {
                    accessibleRollCount++;
                }
            }
        }
        System.out.printf("There are %d accessible rolls.%n", accessibleRollCount);
        return accessibleRollCount;
    }

    @Override
    public Integer part2(Path input, boolean verbose) throws IOException {
        int removableRollCount = 0;
        Grid<Boolean> grid = getPaperRollGrid(input);
        int removedLastTime = -1;
        while (removedLastTime != 0) {
            int removedThisTime = 0;
            for (int i = 0; i < grid.countRows(); i++) {
                for (int j = 0; j < grid.countCols(i); j++) {
                    Boolean isRoll = grid.get(i, j);
                    if (isRoll == null || !isRoll) {
                        continue;
                    }
                    List<Boolean> surrounding = grid.getSurrounding(i, j);
                    if (surrounding.stream().filter(b -> b).count() < 4) {
                        removedThisTime++;
                        grid.set(i, j, false);
                    }
                }
            }
            removableRollCount += removedThisTime;
            removedLastTime = removedThisTime;
        }
        System.out.printf("There are %d removable rolls.%n", removableRollCount);
        return removableRollCount;
    }

    private Grid<Boolean> getPaperRollGrid(Path inputFile) throws IOException {
        Grid<Boolean> grid = new Grid<>();
        for (String line : Files.readAllLines(inputFile)) {
            List<Boolean> row = new ArrayList<>();
            for (char c : line.toCharArray()) {
                row.add(c == '@');
            }
            grid.addRow(row);
        }
        return grid;
    }

    public static class Grid<T> {
        private final List<List<T>> grid = new ArrayList<>();

        public void addRow(List<T> line) {
            grid.add(line);
        }

        public T get(int row, int col) {
            try {
                List<T> rowList = grid.get(row);
                return rowList == null ? null : rowList.get(col);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        public int countRows() {
            return grid.size();
        }

        public int countCols(int row) {
            try {
                List<T> rowList = grid.get(row);
                return rowList == null ? 0 : rowList.size();
            } catch (IndexOutOfBoundsException e) {
                return 0;
            }
        }

        public List<T> getSurrounding(int row, int col) {
            List<T> surrounding = new ArrayList<>();
            surrounding.add(get(row - 1, col - 1));
            surrounding.add(get(row - 1, col));
            surrounding.add(get(row - 1, col + 1));
            surrounding.add(get(row, col - 1));
            surrounding.add(get(row, col + 1));
            surrounding.add(get(row + 1, col - 1));
            surrounding.add(get(row + 1, col));
            surrounding.add(get(row + 1, col + 1));
            return surrounding.stream().filter(Objects::nonNull).toList();
        }

        public void set(int row, int col, T value) {
            List<T> rowList = grid.get(row);
            if (rowList == null) {
                rowList = new ArrayList<>();
                grid.set(row, rowList);
            }
            rowList.set(col, value);
        }
    }
}
