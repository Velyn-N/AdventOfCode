package me.velyn.adventofcode.test.y2025;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.*;

import me.velyn.adventofcode.y2025.*;

public abstract class DefaultTest2025<Part1Return, Part2Return> {

    protected final Puzzle2025<Part1Return, Part2Return> puzzle;
    private final Path inputFile;
    private final Part1Return expectedPart1;
    private final Part2Return expectedPart2;

    public DefaultTest2025(Puzzle2025<Part1Return, Part2Return> puzzle,
                              String inputFile,
                              Part1Return expectedPart1,
                              Part2Return expectedPart2) {
        this.puzzle = puzzle;
        this.inputFile = Path.of(inputFile);
        this.expectedPart1 = expectedPart1;
        this.expectedPart2 = expectedPart2;
    }

    @Test
    public void runDefaultTest1() throws IOException {
        Part1Return result = puzzle.part1(inputFile, true);
        Assertions.assertEquals(expectedPart1, result);
    }

    @Test
    public void runDefaultTest2() throws IOException {
        Part2Return result = puzzle.part2(inputFile, true);
        Assertions.assertEquals(expectedPart2, result);
    }
}
