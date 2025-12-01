package me.velyn.adventofcode;

import static picocli.CommandLine.*;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

import me.velyn.adventofcode.y2025.*;
import picocli.*;

@Command(name = "adventofcode",
        mixinStandardHelpOptions = true,
        version = "adventofcode 1.0",
        description = "Advent of Code implementations by Velyn/Nils in Java",
        subcommands = {

        })
public class AdventOfCodeCli implements Callable<Integer> {

    public static void main(String[] args) {
        System.exit(new CommandLine(new AdventOfCodeCli()).execute(args));
    }

    @Override
    public Integer call() throws IOException {
        new Day1().part1(Path.of("etc/inputs/2025/day1.txt"), true);
        System.out.printf("%nHappy Advent (of Code)!%n");
        return 0;
    }
}
