package me.velyn.adventofcode.test.y2025;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.*;

import me.velyn.adventofcode.y2025.*;

public class Day11Test {

    protected final Day11 puzzle;
    private final Path inputFile;
    private final Path inputFileP2;

    public Day11Test() {
        this.puzzle = new Day11();
        this.inputFile = Path.of("etc/inputs/2025/day11.example.txt");
        this.inputFileP2 = Path.of("etc/inputs/2025/day11.part2.example.txt");
    }

    @Test
    public void runDefaultTest1() throws IOException {
        int result = puzzle.part1(inputFile, true);
        Assertions.assertEquals(5, result);
    }

    @Test
    public void runDefaultTest2() throws IOException {
        long result = puzzle.part2(inputFileP2, true);
        Assertions.assertEquals(2L, result);
    }
}
