package aoc;

import aoc.Graph.UndirectedGraph;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static aoc.Input.forDay;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(12);
        input.fetchInput();
        return input;
    }

    private static boolean isLowerCase(String value) {
        return value.equals(value.toLowerCase());
    }

    UndirectedGraph<String, Void> parseInput(Input input) {
        UndirectedGraph<String, Void> graph = new UndirectedGraph<>();
        input.asListOfStrings().forEach(line -> {
            String[] parts = line.split("-");
            graph.addEdge(parts[0], parts[1]);
        });
        return graph;
    }

    long solution1(Input raw) {
        UndirectedGraph<String, Void> input = parseInput(raw);

        Set<List<String>> paths = input.dfs(
                "start",
                "end",
                (path, next) -> !path.contains(next) || !isLowerCase(next));

        return paths.size();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(10, solution1(input.file("test1.txt")));
        assertEquals(19, solution1(input.file("test2.txt")));
        assertEquals(226, solution1(input.file("test3.txt")));
        assertEquals(3679, solution1(input));
    }

    long solution2(Input raw) {
        UndirectedGraph<String, Void> input = parseInput(raw);

        Set<String> smallCaves = input.connections.keySet().stream().filter(node ->
                        !Set.of("start", "end").contains(node)
                                && isLowerCase(node))
                .collect(toSet());

        Set<List<String>> allPaths = new HashSet<>();
        smallCaves.stream().map(selectedSmallCave ->
                        input.dfs("start", "end",
                                (path, next) -> {
                                    long timesVisited = path.stream().filter(node -> node.equals(next)).count();
                                    boolean small = isLowerCase(next);
                                    return timesVisited == 0
                                            || !small
                                            || (timesVisited == 1 && next.equals(selectedSmallCave));
                                }))
                .forEach(allPaths::addAll);

        return allPaths.size();
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(36, solution2(input.file("test1.txt")));
        assertEquals(103, solution2(input.file("test2.txt")));
        assertEquals(3509, solution2(input.file("test3.txt")));
        assertEquals(107395, solution2(input));
    }
}
