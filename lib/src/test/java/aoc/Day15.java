package aoc;

import aoc.Graph.DirectedGraph;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static aoc.Input.forDay;
import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Day15 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(15);
        input.fetchInput();
        return input;
    }

    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    Long solution1(Input raw) {
        List<String> input = parseInput(raw);

        DirectedGraph<Pos, Long> graph = new DirectedGraph<>();

        int n = tile(input, graph, 1);

        Map<Pos, Long> dijkstra = graph.dijkstraLong(new Pos(0, 0));

        return dijkstra.get(new Pos(n - 1, n - 1));
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(40, solution1(input.file("test1.txt")));
        assertNotEquals(740, solution1(input), "Too high");
        assertEquals(739, solution1(input));
    }

    Long solution2(Input raw) {
        List<String> input = parseInput(raw);

        DirectedGraph<Pos, Long> graph = new DirectedGraph<>();

        int n = tile(input, graph, 5);

        Map<Pos, Long> dijkstra = graph.dijkstraLong(new Pos(0, 0));

        return dijkstra.get(new Pos(n * 5 - 1, n * 5 - 1));
    }

    private int tile(List<String> input, DirectedGraph<Pos, Long> graph, int times) {
        int n = input.size();
        for (int i = 0; i < n; i++) {
            String line = input.get(i);
            for (int j = 0; j < n; j++) {
                long weight = parseLong(line.substring(j, j + 1));
                for (int ik = 0; ik < times; ik++) {
                    for (int jk = 0; jk < times; jk++) {
                        Pos current = new Pos(i + n * ik, j + n * jk);
                        long kWeightIncremented = weight + ik + jk;
                        long kWeight = (kWeightIncremented % 10) + kWeightIncremented / 10;
                        current.adjacentWithoutDiagonals().forEach(adjacent -> {
                            if (adjacent.i() >= 0 && adjacent.j() >= 0) {
                                graph.addEdge(adjacent, current, kWeight);
                            }
                        });
                    }
                }
            }
        }
        return n;
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(315, solution2(input.file("test1.txt")));
        assertEquals(3040, solution2(input));
    }
}
