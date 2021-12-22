package aoc;

import aoc.Day17.Range;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static aoc.Input.forDay;
import static java.lang.Integer.parseInt;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

class Day22 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(22);
        input.fetchInput();
        return input;
    }

    record Cube(Range x, Range y, Range z) {

        public Cube trim(int min, int max) {
            Range range = new Range(min, max);
            return new Cube(x.trim(range), y.trim(range), z.trim(range));
        }

        public boolean isEmpty() {
            return x.length() < 0 || y.length() < 0 || z.length() < 0;
        }

        public long power() {
            if (isEmpty()) {
                return 0;
            }
            return (long) (x.length() + 1) * (y.length() + 1) * (z.length() + 1);
        }

        public Cube intersect(Cube other) {
            return new Cube(
                    x.intersect(other.x),
                    y.intersect(other.y),
                    z.intersect(other.z));
        }

        public boolean contains(Cube other) {
            return x.contains(other.x)
                    && y.contains(other.y)
                    && z.contains(other.z);
        }

        public Set<Cube> without(Cube other) {
            /*
                +-----------------+          +-----------------+
                |   :    4    :   |          |        6        |
                |   +=========+   |          | ~ +=========+ ~ |
              y | 1 |  other  | 3 |        z | 1 |  other  | 3 |
                |   +=========+   |          | ~ +=========+ ~ |
                |   :    2    :   |          |        5        |
                +-----------------+          +-----------------+
                       x                        x
             */
            return Stream.of(
                            /* 1 */   new Cube(new Range(x.min(), other.x.min() - 1), y, other.z),
                            /* 2 */   new Cube(other.x, new Range(y.min(), other.y.min() - 1), other.z),
                            /* 3 */   new Cube(new Range(other.x.max() + 1, x.max()), y, other.z),
                            /* 4 */   new Cube(other.x, new Range(other.y.max() + 1, y.max()), other.z),
                            /* 5 */   new Cube(x, y, new Range(z.min(), other.z.min() - 1)),
                            /* 6 */   new Cube(x, y, new Range(other.z.max() + 1, z.max()))
                    )
                    .map(cube -> new Cube(cube.x.trim(x), cube.y.trim(y), cube.z.trim(z)))
                    .filter(not(Cube::isEmpty))
                    .collect(toSet());
        }
    }

    @Test
    public void cubeOperations() {
        Cube sixty = new Cube(new Range(-60, 60), new Range(-60, 60), new Range(-60, 60));
        Cube fifty = new Cube(new Range(-50, 50), new Range(-50, 50), new Range(-50, 50));

        assertEquals(fifty, sixty.trim(-50, 50));

        Cube zero = new Cube(new Range(0, 0), new Range(0, 0), new Range(0, 0));
        assertEquals(1, zero.power());
        Cube one = new Cube(new Range(0, 1), new Range(0, 1), new Range(0, 1));
        assertEquals(8, one.power());
        assertEquals(zero, zero.intersect(one));
        Set<Cube> diff = one.without(zero);
        assertEquals(3, diff.size());
        assertEquals(7, diff.stream().mapToLong(Cube::power).sum());
        assertEquals(
                Set.of(
                        new Cube(new Range(0, 0), new Range(1, 1), new Range(0, 0)),
                        new Cube(new Range(1, 1), new Range(0, 1), new Range(0, 0)),
                        new Cube(new Range(0, 1), new Range(0, 1), new Range(1, 1))),
                diff);

        Cube cube = new Cube(new Range(0, 9), new Range(0, 19), new Range(0, 29));
        assertFalse(cube.isEmpty());
        assertEquals(6000L, cube.power());

        Cube other = new Cube(new Range(-5, 4), new Range(-10, 9), new Range(-15, 14));
        assertFalse(other.isEmpty());
        assertEquals(6000L, other.power());

        assertFalse(cube.intersect(cube).isEmpty());

        Cube intersect = cube.intersect(other);
        assertEquals(intersect, other.intersect(cube));

        assertEquals(5 * 10 * 15, intersect.power());

        Set<Cube> diff2 = cube.without(other);
        assertEquals(3, diff2.size());

        assertTrue(cube.without(cube).isEmpty());

        Cube minusOne = new Cube(new Range(-1, -1), new Range(-1, -1), new Range(-1, -1));
        assertTrue(cube.intersect(minusOne).isEmpty());
        assertEquals(Set.of(cube), cube.without(minusOne));

        Cube inside = new Cube(new Range(1, 2), new Range(1, 2), new Range(1, 2));
        assertEquals(6, cube.without(inside).size());
    }

    record Instr(boolean on, Cube cube) {
        public Instr trimCube(int min, int max) {
            return new Instr(on, cube.trim(min, max));
        }
    }

    List<Instr> parseInput(Input input) {
        Pattern pattern = compile(
                "(?<on>(on|off)) "
                        + "x=(?<minX>-?\\d+)\\.\\.(?<maxX>-?\\d+),"
                        + "y=(?<minY>-?\\d+)\\.\\.(?<maxY>-?\\d+),"
                        + "z=(?<minZ>-?\\d+)\\.\\.(?<maxZ>-?\\d+)");
        return input.asListOfStrings()
                .stream().map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    assertTrue(matcher.find());
                    return new Instr(
                            matcher.group("on").equals("on"),
                            new Cube(
                                    new Range(parseInt(matcher.group("minX")), parseInt(matcher.group("maxX"))),
                                    new Range(parseInt(matcher.group("minY")), parseInt(matcher.group("maxY"))),
                                    new Range(parseInt(matcher.group("minZ")), parseInt(matcher.group("maxZ")))));
                })
                .toList();
    }

    long solution1(Input raw) {
        List<Instr> input = parseInput(raw)
                .stream()
                .map(instr -> instr.trimCube(-50, 50))
                .filter(instr -> !instr.cube().isEmpty())
                .toList();

        return solve(input);
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(39, solution1(input.file("test2.txt")));
        assertEquals(590784, solution1(input.file("test1.txt")));
        assertEquals(527915, solution1(input));
    }

    long solution2(Input raw) {
        List<Instr> input = parseInput(raw);

        return solve(input);
    }

    private long solve(List<Instr> input) {
        Set<Cube> onCubes = new HashSet<>();

        for (Instr instr : input) {

            Set<Cube> onCubesToRemove = new HashSet<>();
            Set<Cube> onCubesToAdd = new HashSet<>();

            if (instr.on()) {
                onCubesToAdd.add(instr.cube());
            }

            for (Cube cube : onCubes) {
                Cube intersection = cube.intersect(instr.cube());
                if (!intersection.isEmpty()) {
                    //  Replace with split cubes
                    onCubesToRemove.add(cube);
                    onCubesToAdd.addAll(cube.without(intersection));
                }
            }

            onCubes.removeAll(onCubesToRemove);
            onCubes.addAll(onCubesToAdd);
        }

        return onCubes.stream().mapToLong(Cube::power).sum();
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(2758514936282235L, solution2(input.file("test3.txt")));
        assertEquals(1218645427221987L, solution2(input));
    }
}
