package aoc;

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
}
