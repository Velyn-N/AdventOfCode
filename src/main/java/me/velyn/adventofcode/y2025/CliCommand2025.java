package me.velyn.adventofcode.y2025;

import picocli.CommandLine.*;

@Command(
        name = "2025",
        subcommands = {
                Day1.class,
                Day2.class,
                Day3.class,
                Day4.class,
                Day5.class,
                Day6.class,
                Day7.class,
        }
)
public class CliCommand2025 {
}
