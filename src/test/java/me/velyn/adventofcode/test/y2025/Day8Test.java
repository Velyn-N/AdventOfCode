package me.velyn.adventofcode.test.y2025;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.*;

import me.velyn.adventofcode.y2025.*;

public class Day8Test {

    protected final Day8 puzzle;
    private final Path inputFile;

    public Day8Test() {
        this.puzzle = new Day8();
        this.inputFile = Path.of("etc/inputs/2025/day8.example.txt");
    }

    @Test
    public void runDefaultTest1() throws IOException {
        Long result = puzzle.part1(inputFile, 10);
        Assertions.assertEquals(40L, result);
    }

    @Test
    public void runDefaultTest2() throws IOException {
        Long result = puzzle.part2(inputFile);
        Assertions.assertEquals(25272L, result);
    }
}
