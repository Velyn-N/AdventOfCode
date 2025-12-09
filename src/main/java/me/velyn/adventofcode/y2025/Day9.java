package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day9")
public class Day9 implements Puzzle2025<Long, Long> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day9.example.txt")
    private Path inputFile;

    @Option(names = {"-v", "--verbose"})
    private boolean verbose;

    @Override
    public Integer call() throws Exception {
        part1(inputFile, verbose);
        System.out.println();
        part2(inputFile, verbose);
        return 0;
    }

    @Override
    public Long part1(Path input, boolean verbose) throws IOException {
        List<Coordinate> redTiles = parseInputs(input);
        long biggestArea = 0;
        Coordinate corner1 = null;
        Coordinate corner2 = null;
        for (Coordinate firstCoord : redTiles) {
            for (Coordinate secondCoord : redTiles) {
                long diffX = Math.abs(firstCoord.x - secondCoord.x);
                long diffY = Math.abs(firstCoord.y - secondCoord.y);
                long edgeX = diffX + 1;
                long edgeY = diffY + 1;
                long area = edgeX * edgeY;
                if (area > biggestArea) {
                    biggestArea = area;
                    corner1 = firstCoord;
                    corner2 = secondCoord;
                }
            }
        }
        System.out.printf("The largest area of red tiles is %d, with corners at %s and %s%n",
                biggestArea, corner1, corner2);
        return biggestArea;
    }

    @Override
    public Long part2(Path input, boolean verbose) throws IOException {
        List<Coordinate> redTiles = parseInputs(input);

        List<VerticalWall> verticalWalls = new ArrayList<>();
        Map<Long, List<HorizontalWallPart>> horizontalWallsByRow = new HashMap<>();

        for (int i = 0; i < redTiles.size(); i++) {
            Coordinate start = redTiles.get(i);
            Coordinate end = i + 1 == redTiles.size() ? redTiles.getFirst() : redTiles.get(i + 1);

            boolean isVertical = start.x == end.x;
            if (isVertical) {
                long minY = Math.min(start.y, end.y);
                long maxY = Math.max(start.y, end.y);
                boolean isGoingDown = end.y > start.y;
                verticalWalls.add(new VerticalWall(start.x, minY, maxY, isGoingDown));
            } else {
                long y = start.y;
                long minX = Math.min(start.x, end.x);
                long maxX = Math.max(start.x, end.x);
                horizontalWallsByRow.putIfAbsent(y, new ArrayList<>());
                horizontalWallsByRow.get(y).add(new HorizontalWallPart(minX, maxX));
            }
        }

        List<PossibleRectangle> possibleRectangles = getPossibleRectangles(redTiles);
        possibleRectangles.sort((r1, r2) -> Long.compare(r2.area, r1.area));

        if (verbose) {
            System.out.printf("Checking %d possible rectangles...%n", possibleRectangles.size());
        }

        Map<Long, List<SafeRange>> cachedSafeRangesPerRow = new HashMap<>();

        for (PossibleRectangle rect : possibleRectangles) {
            boolean rectangleIsFullySafe = true;

            for (long y = rect.minY; y <= rect.maxY; y++) {
                if (!cachedSafeRangesPerRow.containsKey(y)) {
                    List<SafeRange> safeRanges = findSafeRangesForRow(y, verticalWalls, horizontalWallsByRow.get(y));
                    cachedSafeRangesPerRow.put(y, safeRanges);
                }
                List<SafeRange> safeRanges = cachedSafeRangesPerRow.get(y);

                boolean rowIsSafe = false;
                for (SafeRange range : safeRanges) {
                    if (rect.minX >= range.minX && rect.maxX <= range.maxX) {
                        rowIsSafe = true;
                        break;
                    }
                }

                if (!rowIsSafe) {
                    rectangleIsFullySafe = false;
                    break;
                }
            }

            if (rectangleIsFullySafe) {
                System.out.printf("The largest safe area is %d with corners at (%d,%d) and (%d,%d)%n",
                        rect.area, rect.minX, rect.minY, rect.maxX, rect.maxY);
                return rect.area;
            }
        }
        System.out.println("No safe rectangle found!");
        return -1L;
    }

    private List<PossibleRectangle> getPossibleRectangles(List<Coordinate> redTiles) {
        List<PossibleRectangle> possibleRectangles = new ArrayList<>();
        for (Coordinate corner1 : redTiles) {
            for (Coordinate corner2 : redTiles) {
                long minX = Math.min(corner1.x, corner2.x);
                long maxX = Math.max(corner1.x, corner2.x);
                long minY = Math.min(corner1.y, corner2.y);
                long maxY = Math.max(corner1.y, corner2.y);

                long width = maxX - minX + 1;
                long height = maxY - minY + 1;
                long area = width * height;
                possibleRectangles.add(new PossibleRectangle(minX, maxX, minY, maxY, area));
            }
        }
        return possibleRectangles;
    }

    private List<SafeRange> findSafeRangesForRow(long rowNumber,
                                                 List<VerticalWall> verticalWalls,
                                                 List<HorizontalWallPart> horizontalWalls) {
        List<SafeRange> safeRanges = new ArrayList<>();

        List<VerticalWall> crossingWalls = new ArrayList<>();
        for (VerticalWall wall : verticalWalls) {
            if (wall.minY <= rowNumber && wall.maxY > rowNumber) {
                crossingWalls.add(wall);
            }
        }
        crossingWalls.sort(Comparator.comparingLong(w -> w.x));

        long lastWallX = -1;
        int windingNumber = 0;

        for (VerticalWall wall : crossingWalls) {
            if (windingNumber != 0) {
                safeRanges.add(new SafeRange(lastWallX, wall.x));
            }
            lastWallX = wall.x;
            windingNumber += wall.goingDown ? 1 : -1;
        }

        if (horizontalWalls != null) {
            for (HorizontalWallPart segment : horizontalWalls) {
                safeRanges.add(new SafeRange(segment.minX, segment.maxX));
            }
        }

        if (safeRanges.isEmpty()) {
            return safeRanges;
        }

        safeRanges.sort(Comparator.comparingLong(r -> r.minX));
        return mergeSafeRanges(safeRanges);
    }

    private List<SafeRange> mergeSafeRanges(List<SafeRange> safeRanges) {
        List<SafeRange> mergedRanges = new ArrayList<>();
        SafeRange currentRange = safeRanges.getFirst();

        for (int i = 1; i < safeRanges.size(); i++) {
            SafeRange nextRange = safeRanges.get(i);
            if (nextRange.minX <= currentRange.maxX + 1) {
                currentRange = new SafeRange(currentRange.minX, Math.max(currentRange.maxX, nextRange.maxX));
            } else {
                mergedRanges.add(currentRange);
                currentRange = nextRange;
            }
        }
        mergedRanges.add(currentRange);
        return mergedRanges;
    }

    private List<Coordinate> parseInputs(Path input) throws IOException {
        List<Coordinate> coordinates = new ArrayList<>();
        for (String line : Files.readAllLines(input)) {
            String[] split = line.split(",");
            if (split.length != 2) {
                continue;
            }
            coordinates.add(new Coordinate(NumberUtils.toLong(split[0]), NumberUtils.toLong(split[1])));
        }
        return coordinates;
    }

    public record PossibleRectangle(long minX, long maxX, long minY, long maxY, long area) {}

    public record VerticalWall(long x, long minY, long maxY, boolean goingDown) {}

    public record HorizontalWallPart(long minX, long maxX) {}

    public record SafeRange(long minX, long maxX) {}

    public record Coordinate(long x, long y) {}
}
