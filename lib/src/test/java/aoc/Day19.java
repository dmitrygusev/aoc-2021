package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;

import static aoc.Input.forDay;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day19 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(19);
        input.fetchInput();
        return input;
    }

    record Point3D(int x, int y, int z) implements Comparable<Point3D> {
        @Override
        public int compareTo(Point3D o) {
            Comparator<Point3D> cx = comparingInt(Point3D::x);
            Comparator<Point3D> cy = comparingInt(Point3D::y);
            Comparator<Point3D> cz = comparingInt(Point3D::z);
            return cx.thenComparing(cy).thenComparing(cz).compare(this, o);
        }

        public Point3D roll() {
            return new Point3D(x, z, -y);
        }

        @SuppressWarnings("SuspiciousNameCombination")
        public Point3D turn() {
            return new Point3D(-y, x, z);
        }

        // https://stackoverflow.com/questions/16452383/how-to-get-all-24-rotations-of-a-3-dimensional-array
        public List<Point3D> allTurnsAndRotations() {
            Point3D p = this;
            List<Point3D> result = new ArrayList<>();
            for (int cycle = 0; cycle < 2; cycle++) {
                for (int step = 0; step < 3; step++) {
                    p = p.roll();
                    result.add(p);
                    for (int i = 0; i < 3; i++) {
                        p = p.turn();
                        result.add(p);
                    }
                }
                p = p.roll().turn().roll();
            }
            return result;
        }

        public Point3D subtract(Point3D other) {
            return new Point3D(x - other.x, y - other.y, z - other.z);
        }

        public Point3D add(Point3D other) {
            return new Point3D(x + other.x, y + other.y, z + other.z);
        }

        int manhattanDistance(Point3D other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y) + Math.abs(other.z - z);
        }
    }

    static class Scanner {
        final String id;

        Set<Point3D> beacons = new TreeSet<>();

        Scanner(String id) {
            this.id = id;
        }

        public void add(Point3D point3D) {
            beacons.add(point3D);
        }

        public Point3D match(Scanner other) {
            Map<Point3D, Integer> map = new HashMap<>();
            beacons.forEach(base -> {
                other.beacons.stream()
                        .map(base::subtract)
                        .forEach(diff -> map.merge(diff, 1, Integer::sum));
            });

            List<Entry<Point3D, Integer>> matches =
                    map.entrySet().stream()
                            .filter(entry -> entry.getValue() >= 12)
                            .toList();

            if (matches.size() == 0) {
                return null;
            }

            assert matches.size() == 1;

            return matches.get(0).getKey();
        }

        public List<Scanner> allTurnsAndRotations() {
            List<Scanner> result = new ArrayList<>();
            beacons.stream()
                    .map(Point3D::allTurnsAndRotations)
                    .forEach(transformations -> {
                        if (result.isEmpty()) {
                            transformations.forEach($ ->
                                    result.add(new Scanner(id + "." + result.size())));
                        }
                        for (int i = 0; i < transformations.size(); i++) {
                            result.get(i).add(transformations.get(i));
                        }
                    });
            return result;
        }

        private void addAll(Collection<Point3D> beacons) {
            this.beacons.addAll(beacons);
        }

        public Set<Point3D> beaconsWithOffset(Point3D offset) {
            return beacons.stream().map(b -> b.add(offset)).collect(toSet());
        }
    }

    List<Scanner> parseInput(Input input) {
        List<Scanner> scanners = new ArrayList<>();
        input.asListOfStrings().forEach(line -> {
            if (line.contains("scanner")) {
                scanners.add(new Scanner(valueOf(scanners.size())));
                return;
            }
            String[] parts = line.split(",");
            if (parts.length == 3) {
                scanners
                        .get(scanners.size() - 1)
                        .add(new Point3D(
                                parseInt(parts[0]),
                                parseInt(parts[1]),
                                parseInt(parts[2])));
            }
        });
        return scanners;
    }

    int solution1(Input raw) {
        List<Scanner> input = parseInput(raw);

        Scanner base = input.remove(0);

        while (input.size() > 0) {
            outer:
            for (Scanner scanner : input) {
                for (Scanner rotated : scanner.allTurnsAndRotations()) {
                    Point3D scannerPos = base.match(rotated);
                    if (scannerPos != null) {
                        base.addAll(rotated.beaconsWithOffset(scannerPos));
                        input.remove(scanner);
                        break outer;
                    }
                }
            }
        }

        return base.beacons.size();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(79, solution1(input.file("test1.txt")));
        assertEquals(381, solution1(input));
    }

    int solution2(Input raw) {
        List<Scanner> input = parseInput(raw);

        List<Point3D> scanners = new ArrayList<>();

        Scanner base = input.remove(0);

        while (input.size() > 0) {
            outer:
            for (Scanner scanner : input) {
                for (Scanner rotated : scanner.allTurnsAndRotations()) {
                    Point3D scannerPos = base.match(rotated);
                    if (scannerPos != null) {
                        scanners.add(scannerPos);
                        base.addAll(rotated.beaconsWithOffset(scannerPos));
                        input.remove(scanner);
                        break outer;
                    }
                }
            }
        }

        int max = 0;
        for (int i = 0; i < scanners.size(); i++) {
            for (int j = i; j < scanners.size(); j++) {
                int distance = scanners.get(i).manhattanDistance(scanners.get(j));
                if (distance > max) {
                    max = distance;
                }
            }
        }

        return max;
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(3621, solution2(input.file("test1.txt")));
        assertEquals(12201, solution2(input));
    }
}
