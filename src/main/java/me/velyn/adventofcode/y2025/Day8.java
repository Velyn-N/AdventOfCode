package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day8")
public class Day8 implements Callable<Integer> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day8.example.txt")
    private Path inputFile;

    @Option(names = {"-c", "--connections"},
            description = "Number of connections to make for part 1 (10 for example, 1000 for real input)")
    private Integer numConnections;

    @Override
    public Integer call() throws Exception {
        part1(inputFile, numConnections);
        System.out.println();
        part2(inputFile);
        return 0;
    }

    public Long part1(Path input, int numConnections) throws IOException {
        List<Box> boxes = toBoxes(parseInput(input));
        List<Connection> connections = connectAllBoxes(boxes);

        connections.sort(Comparator.comparingLong(e -> e.distance));

        int connectionsToDo = Math.min(numConnections, connections.size());
        for (int connectionCount = 0; connectionCount < connectionsToDo; connectionCount++) {
            Connection connection = connections.get(connectionCount);
            mergeGroups(boxes, connection.box1Index, connection.box2Index);
        }

        List<Long> sizes = boxes.stream()
                .collect(Collectors.groupingBy(b -> b.groupId, Collectors.counting()))
                .values()
                .stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        long result = 1;
        for (int i = 0; i < sizes.size() && i < 3; i++) {
            result *= sizes.get(i);
        }
        
        System.out.printf("The three largest groups sizes multiplied are %d%n", result);
        return result;
    }

    public Long part2(Path input) throws IOException {
        List<Box> boxes = toBoxes(parseInput(input));
        List<Connection> connections = connectAllBoxes(boxes);

        connections.sort(Comparator.comparingLong(e -> e.distance));

        int groupCount = boxes.size();
        Connection lastMergeConnection = null;

        for (Connection connection : connections) {
            Box box1 = boxes.get(connection.box1Index);
            Box box2 = boxes.get(connection.box2Index);

            if (box1.groupId != box2.groupId) {
                mergeGroups(boxes, connection.box1Index, connection.box2Index);
                groupCount--;
                lastMergeConnection = connection;

                if (groupCount == 1) {
                    break;
                }
            }
        }

        if (lastMergeConnection == null) {
            throw new IllegalStateException("Something went wrong, lastMergeConnection is still null!");
        }

        Box box1 = boxes.get(lastMergeConnection.box1Index);
        Box box2 = boxes.get(lastMergeConnection.box2Index);
        long result = (long) box1.pos.x * box2.pos.x;

        System.out.printf("The multiplied coordinates of the last two junction boxes is: %d%n", result);
        return result;
    }

    private List<Connection> connectAllBoxes(List<Box> boxes) {
        List<Connection> connections = new ArrayList<>();

        for (int box1Iter = 0; box1Iter < boxes.size(); box1Iter++) {
            Box boxA = boxes.get(box1Iter);

            for (int box2Iter = box1Iter + 1; box2Iter < boxes.size(); box2Iter++) {
                Box boxB = boxes.get(box2Iter);

                long distance = squaredDistance(boxA.pos, boxB.pos);
                connections.add(new Connection(distance, box1Iter, box2Iter));
            }
        }
        return connections;
    }

    private long squaredDistance(Vector a, Vector b) {
        long distX = (long) a.x - b.x;
        long distY = (long) a.y - b.y;
        long distZ = (long) a.z - b.z;
        return (distX * distX) + (distY * distY) + (distZ * distZ);
    }

    private void mergeGroups(List<Box> boxes, int box1Index, int box2Index) {
        int group1 = boxes.get(box1Index).groupId;
        int group2 = boxes.get(box2Index).groupId;

        if (group1 == group2) {
            return;
        }

        for (Box box : boxes) {
            if (box.groupId == group2) {
                box.groupId = group1;
            }
        }
    }

    private List<Vector> parseInput(Path input) throws IOException {
        List<Vector> vectors = new ArrayList<>();
        for (String line : Files.readAllLines(input)) {
            String[] split = line.split(",");
            if (split.length != 3) {
                continue;
            }
            vectors.add(new Vector(
                    NumberUtils.toInt(split[0]),
                    NumberUtils.toInt(split[1]),
                    NumberUtils.toInt(split[2])
            ));
        }
        return vectors;
    }

    private List<Box> toBoxes(List<Vector> vectors) {
        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            boxes.add(new Box(vectors.get(i), i));
        }
        return boxes;
    }

    public static class Box {
        public final Vector pos;
        public int groupId;

        public Box(Vector pos, int groupId) {
            this.pos = pos;
            this.groupId = groupId;
        }
    }

    public record Connection(long distance, int box1Index, int box2Index) {}

    public record Vector(int x, int y, int z) {}
}
