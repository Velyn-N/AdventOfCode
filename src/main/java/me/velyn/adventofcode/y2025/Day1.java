package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;

import org.apache.commons.lang3.math.*;

public class Day1 implements Puzzle2025<Integer, Integer> {

    @Override
    public Integer part1(Path input, boolean verbose) throws IOException {
        var result = unlock(input, verbose);
        System.out.printf("The Password is: %d, the dial is at %d", result.zeroCount, result.dialValue);
        return result.zeroCount;
    }

    @Override
    public Integer part2(Path input, boolean verbose) throws IOException {
        var result = unlock(input, verbose);
        System.out.printf("The Password is: %d, the dial is at %d", result.zeroPassCount, result.dialValue);
        return result.zeroPassCount;
    }

    private Result unlock(Path input, boolean verbose) throws IOException {
        int zeroCount = 0;
        int zeroPassCount = 0;

        int dialValue = 50;

        for (String line : Files.readAllLines(input)) {
            int zeroPasses = 0;
            boolean isLeft = line.startsWith("L");
            int turns = NumberUtils.toInt(line.substring(1));

            for (int i = 0; i < turns; i++) {
                if (isLeft) {
                    dialValue--;
                    if (dialValue < 0) {
                        dialValue = 99;
                    }
                } else {
                    dialValue++;
                    if (dialValue > 99) {
                        dialValue = 0;
                    }
                }

                if (dialValue == 0) {
                    zeroPasses++;
                }
            }

            if (dialValue == 0) {
                zeroCount++;
            }
            zeroPassCount += zeroPasses;
            if (verbose) {
                System.out.printf("%5s -> %2s (%s zeros)%n", line, dialValue, zeroPasses);
            }
        }
        return new Result(zeroCount, zeroPassCount, dialValue);
    }

    record Result(int zeroCount, int zeroPassCount, int dialValue) {}
}
