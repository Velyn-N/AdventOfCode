# Advent of Code

This repository contains my implementations of the [Advent of Code](https://adventofcode.com) Event.

The project runs on Java 21 and Maven. Once compiled you can run the different Puzzles using this Command:

```shell
java -jar target/adventofcode-1.0-SNAPSHOT.jar <year> <day> (-f <inputfile>) (-v)
```

Example:

```shell
java -jar target/adventofcode-1.0-SNAPSHOT.jar 2025 day1 -f etc/inputs/2025/day1.example.txt
```

You can also specify the `-v` option to see additional logging.
Omitting the `-f` option will load the corresponding `.example.txt` file from the `etc/inputs/` folder.
