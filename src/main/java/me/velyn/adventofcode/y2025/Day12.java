package me.velyn.adventofcode.y2025;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;

import picocli.CommandLine.*;

@Command(name = "day12")
public class Day12 implements Puzzle2025<Integer, Integer> {

    @Option(names = {"-f", "--file"},
            defaultValue = "etc/inputs/2025/day12.example.txt")
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
    public Integer part1(Path input, boolean verbose) throws IOException {
        Input parsedInput = parseInput(input);
        if (verbose) {
            System.out.printf("Parsed %d shapes and %d regions%n", parsedInput.shapes.size(), parsedInput.regions.size());
        }

        Map<Integer, Shape> shapesById = parsedInput.shapes.stream()
                .collect(Collectors.toMap(Shape::id, s -> s));

        Map<Integer, List<Shape>> preCalculatedShapes = parsedInput.shapes.stream()
                .collect(Collectors.toMap(Shape::id, this::preCalcOrientations));

        Shape slackShape = new Shape(-1, List.of(new Point(0, 0)), 1, 1);
        preCalculatedShapes.put(-1, List.of(slackShape));
        shapesById.put(-1, slackShape);

        int canFit = 0;

        for (Region region : parsedInput.regions) {
            if (canFitRegion(region, shapesById, preCalculatedShapes)) {
                canFit++;
            }
        }

        System.out.printf("%d Regions can fit all the presents they are required to fit%n", canFit);
        return canFit;
    }

    @Override
    public Integer part2(Path input, boolean verbose) throws IOException {
        System.out.println("There was no Part 2 Puzzle for this Day");
        return 1;
    }

    private boolean canFitRegion(Region region,
                                 Map<Integer, Shape> shapesById,
                                 Map<Integer, List<Shape>> preCalculatedShapes) {
        int regionArea = region.width * region.height;

        int neededArea = 0;
        Map<Integer, Integer> currentCounts = new HashMap<>();
        
        for (int shapeId = 0; shapeId < region.neededPresents.size(); shapeId++) {
            int count = region.neededPresents.get(shapeId);
            if (count > 0) {
                Shape base = shapesById.get(shapeId);
                neededArea += base.area() * count;
                currentCounts.put(shapeId, count);
            }
        }

        if (neededArea > regionArea) {
            return false;
        }

        int slackCount = regionArea - neededArea;
        if (slackCount > 0) {
            currentCounts.put(-1, slackCount);
        }

        List<BitSet> grid = new ArrayList<>();
        for (int y = 0; y < region.height; y++) {
            grid.add(new BitSet(region.width));
        }

        List<Integer> idsInPlay = new ArrayList<>(currentCounts.keySet());
        idsInPlay.sort((id1, id2) -> {
            if (id1 == -1) return 1;
            if (id2 == -1) return -1;
            int area1 = shapesById.get(id1).area();
            int area2 = shapesById.get(id2).area();
            return Integer.compare(area2, area1);
        });

        return backtrack(region, grid, currentCounts, idsInPlay, preCalculatedShapes);
    }

    private boolean backtrack(Region region,
                              List<BitSet> grid,
                              Map<Integer, Integer> currentCounts,
                              List<Integer> idsInPlay,
                              Map<Integer, List<Shape>> preCalculatedShapes) {
        
        boolean onlySlackLeft = true;
        for (Map.Entry<Integer, Integer> entry : currentCounts.entrySet()) {
            if (entry.getKey() != -1 && entry.getValue() > 0) {
                onlySlackLeft = false;
                break;
            }
        }
        
        if (onlySlackLeft) {
            return true;
        }

        Point nextEmpty = findNextEmpty(region, grid);
        if (nextEmpty == null) {
            return true;
        }

        int targetX = nextEmpty.x;
        int targetY = nextEmpty.y;

        for (int shapeId : idsInPlay) {
            int count = currentCounts.get(shapeId);
            if (count <= 0) {
                continue;
            }

            List<Shape> orientations = preCalculatedShapes.get(shapeId);
            for (Shape oriented : orientations) {
                for (Point anchorCell : oriented.cells) {
                    int dx = targetX - anchorCell.x;
                    int dy = targetY - anchorCell.y;

                    if (dx + oriented.width <= 0 || dy + oriented.height <= 0) continue;

                    if (coversPrevious(oriented, dx, dy, targetX, targetY)) {
                         continue;
                    }

                    if (canPlace(region, grid, oriented, dx, dy)) {
                        place(grid, oriented, dx, dy, true);
                        currentCounts.put(shapeId, count - 1);

                        if (backtrack(region, grid, currentCounts, idsInPlay, preCalculatedShapes)) {
                            return true;
                        }

                        currentCounts.put(shapeId, count);
                        place(grid, oriented, dx, dy, false);
                    }
                }
            }
        }
        return false;
    }

    private boolean coversPrevious(Shape s, int dx, int dy, int targetX, int targetY) {
        for (Point p : s.cells) {
            int x = p.x + dx;
            int y = p.y + dy;
            
            if (y < targetY) return true;
            if (y == targetY && x < targetX) return true;
        }
        return false;
    }

    private Point findNextEmpty(Region region, List<BitSet> grid) {
        for (int y = 0; y < region.height; y++) {
            for (int x = 0; x < region.width; x++) {
                if (!grid.get(y).get(x)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    private boolean canPlace(Region region, List<BitSet> grid, Shape shape, int dx, int dy) {
        for (Point p : shape.cells) {
            int x = p.x + dx;
            int y = p.y + dy;

            if (x < 0 || x >= region.width || y < 0 || y >= region.height) {
                return false;
            }
            if (grid.get(y).get(x)) {
                return false;
            }
        }
        return true;
    }

    private void place(List<BitSet> grid, Shape shape, int dx, int dy, boolean value) {
        for (Point p : shape.cells) {
            int x = p.x + dx;
            int y = p.y + dy;
            grid.get(y).set(x, value);
        }
    }

    private List<Shape> preCalcOrientations(Shape s) {
        Map<String, Shape> unique = new LinkedHashMap<>();

        Shape cur = normalizeTo00(s);
        for (int i = 0; i < 4; i++) {
            Shape normalized = normalizeTo00(cur);
            unique.putIfAbsent(fingerprint(normalized), normalized);
            cur = rotate90(cur);
        }

        Shape mirrored = mirrorX(s);
        cur = normalizeTo00(mirrored);
        for (int i = 0; i < 4; i++) {
            Shape normalized = normalizeTo00(cur);
            unique.putIfAbsent(fingerprint(normalized), normalized);
            cur = rotate90(cur);
        }
        return new ArrayList<>(unique.values());
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Shape rotate90(Shape s) {
        List<Point> rotated = new ArrayList<>(s.cells.size());
        for (Point p : s.cells) {
            int x2 = s.height - 1 - p.y;
            int y2 = p.x;
            rotated.add(new Point(x2, y2));
        }
        return new Shape(s.id, rotated, s.height, s.width);
    }

    private Shape mirrorX(Shape s) {
        List<Point> mirrored = new ArrayList<>(s.cells.size());
        for (Point p : s.cells) {
            int newX = s.width - 1 - p.x;
            mirrored.add(new Point(newX, p.y));
        }
        return new Shape(s.id, mirrored, s.width, s.height);
    }

    private Shape normalizeTo00(Shape s) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point p : s.cells) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        List<Point> normalized = new ArrayList<>(s.cells.size());
        for (Point p : s.cells) {
            normalized.add(new Point(p.x - minX, p.y - minY));
        }

        normalized.sort(Comparator.<Point>comparingInt(pt -> pt.y).thenComparingInt(pt -> pt.x));

        int width = (maxX - minX) + 1;
        int height = (maxY - minY) + 1;
        return new Shape(s.id, normalized, width, height);
    }

    private String fingerprint(Shape s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.width).append("x").append(s.height).append(":");
        for (Point p : s.cells) {
            sb.append(p.x).append(",").append(p.y).append(";");
        }
        return sb.toString();
    }

    private Input parseInput(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input);

        int lineIndex = 0;

        List<Shape> shapes = new ArrayList<>();

        while (lineIndex < lines.size() && StringUtils.isNotBlank(lines.get(lineIndex))) {
            String line = lines.get(lineIndex);
            if (line.contains("x")) {
                break;
            }
            lineIndex++;

            int id = NumberUtils.toInt(line.replace(":", ""));

            List<String> shapeDesc = new ArrayList<>();
            while (lineIndex < lines.size() && StringUtils.isNotBlank(lines.get(lineIndex))) {
                shapeDesc.add(lines.get(lineIndex));
                lineIndex++;
            }

            shapes.add(parseShape(id, shapeDesc));

            while (lineIndex < lines.size() && StringUtils.isBlank(lines.get(lineIndex))) {
                lineIndex++;
            }
        }

        List<Region> regions = new ArrayList<>();

        for (int i = lineIndex; i < lines.size(); i++) {
            regions.add(parseRegion(lines.get(i)));
        }
        return new Input(shapes, regions);
    }

    private Shape parseShape(int id, List<String> shapeDesc) {
        int height = shapeDesc.size();
        int width = shapeDesc.getFirst().length();

        List<Point> cells = new ArrayList<>();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                char c = shapeDesc.get(h).charAt(w);
                if (c == '#') {
                    cells.add(new Point(w, h));
                }
            }
        }
        return new Shape(id, cells, width, height);
    }

    private Region parseRegion(String line) {
        String[] words = line.split(" ");
        int height = 0;
        int width = 0;
        List<Integer> neededPresents = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                String[] sizes = word.replace(":", "").split("x");
                width = NumberUtils.toInt(sizes[0]);
                height = NumberUtils.toInt(sizes[1]);
            } else {
                neededPresents.add(NumberUtils.toInt(word));
            }
        }
        return new Region(height, width, neededPresents);
    }

    public record Input(List<Shape> shapes, List<Region> regions) {}

    public record Shape(int id, List<Point> cells, int width, int height) {
        public int area() {
            return cells.size();
        }
    }

    public record Point(int x, int y) {}

    public record Region(int height, int width, List<Integer> neededPresents) {}
}
