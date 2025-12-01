package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

public interface Puzzle2025<Part1Return, Part2Return> extends Callable<Integer> {

    Part1Return part1(Path input, boolean verbose) throws IOException;

    Part2Return part2(Path input, boolean verbose) throws IOException;
}
