package aoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record Pos(int i, int j) {
    //  TODO Static pool
    private static final Map<Integer, Map<Integer, Pos>> staticCache = new HashMap<>();

    public static Pos of(int i, int j) {
        if (i >= 0 && i <= 100 &&
                j >= 0 && j <= 100) {
            return staticCache
                    .computeIfAbsent(i, $ -> new HashMap<>())
                    .computeIfAbsent(j, $ -> new Pos(i, j));
        }
        return new Pos(i, j);
    }

    public Set<Pos> adjacentWithDiagonals() {
        return Set.of(
                new Pos(i - 1, j),
                new Pos(i - 1, j + 1),
                new Pos(i, j + 1),
                new Pos(i + 1, j + 1),
                new Pos(i + 1, j),
                new Pos(i + 1, j - 1),
                new Pos(i, j - 1),
                new Pos(i - 1, j - 1));
    }

    public Set<Pos> adjacentWithoutDiagonals() {
        return Set.of(
                new Pos(i - 1, j),
                new Pos(i + 1, j),
                new Pos(i, j + 1),
                new Pos(i, j - 1));
    }

    public List<Pos> surrounding3x3Ordered() {
        return List.of(
                new Pos(i - 1, j - 1),
                new Pos(i - 1, j),
                new Pos(i - 1, j + 1),
                new Pos(i, j - 1),
                new Pos(i, j),
                new Pos(i, j + 1),
                new Pos(i + 1, j - 1),
                new Pos(i + 1, j),
                new Pos(i + 1, j + 1));
    }

    public boolean within(int minI, int maxI, int minJ, int maxJ) {
        return (minI <= i && i <= maxI) && (minJ <= j && j <= maxJ);
    }

    public int manhattanDistanceTo(Pos to) {
        return Math.abs(i - to.i) + Math.abs(j - to.j);
    }
}
