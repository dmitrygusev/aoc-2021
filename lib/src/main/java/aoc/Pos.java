package aoc;

import java.util.List;
import java.util.Set;

public record Pos(int i, int j) {
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
}
