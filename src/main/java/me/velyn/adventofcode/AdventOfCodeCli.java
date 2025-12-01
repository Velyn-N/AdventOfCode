package me.velyn.adventofcode;

import me.velyn.adventofcode.y2025.*;
import picocli.*;
import picocli.CommandLine.*;

@Command(
        name = "adventofcode",
        mixinStandardHelpOptions = true,
        version = "adventofcode 1.0",
        description = "Advent of Code implementations by Velyn/Nils in Java",
        subcommands = {
                CliCommand2025.class
        }
)
public class AdventOfCodeCli {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static void main(String[] args) {
        System.exit(new CommandLine(new AdventOfCodeCli()).execute(args));
    }
}
