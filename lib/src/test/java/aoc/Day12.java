package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static aoc.Input.forDay;
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

    record Node(String name) {
    }

    static class UndirectedGraph {
        Map<Node, List<Node>> connections = new HashMap<>();

        public void addEdge(Node a, Node b) {
            connections.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            connections.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }

        //  Spanning trees
        public Set<List<Node>> dfs(Node start, Node end, BiFunction<Stack<Node>, Node, Boolean> canVisit) {
            HashSet<List<Node>> allPaths = new HashSet<>();
            dfs(new Stack<>(), start, end, allPaths, canVisit);
            return allPaths;
        }

        private void dfs(
                Stack<Node> path,
                Node next,
                Node end,
                Set<List<Node>> paths,
                BiFunction<Stack<Node>, Node, Boolean> canVisit) {

            if (!canVisit.apply(path, next)) return;

            path.push(next);
            try {
                if (next.equals(end)) {
                    paths.add(List.copyOf(path));
                    return;
                }
                List<Node> adjacent = connections.get(next);
                adjacent.forEach(target -> dfs(path, target, end, paths, canVisit));
            } finally {
                path.pop();
            }
        }
    }

    private static boolean isLowerCase(String value) {
        return value.equals(value.toLowerCase());
    }

    UndirectedGraph parseInput(Input input) {
        UndirectedGraph graph = new UndirectedGraph();
        input.asListOfStrings().forEach(line -> {
            String[] parts = line.split("-");
            graph.addEdge(new Node(parts[0]), new Node(parts[1]));
        });
        return graph;
    }

    long solution1(Input raw) {
        UndirectedGraph input = parseInput(raw);

        Set<List<Node>> paths = input.dfs(
                new Node("start"),
                new Node("end"),
                (path, next) -> !path.contains(next) || !isLowerCase(next.name));

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
        UndirectedGraph input = parseInput(raw);

        Set<Node> smallCaves = input.connections.keySet().stream().filter(node ->
                        !Set.of("start", "end").contains(node.name)
                                && isLowerCase(node.name))
                .collect(Collectors.toSet());

        Set<List<Node>> allPaths = new HashSet<>();
        smallCaves.stream().map(selectedSmallCave ->
                        input.dfs(new Node("start"), new Node("end"),
                                (path, next) -> {
                                    long timesVisited = path.stream().filter(node -> node.equals(next)).count();
                                    boolean small = isLowerCase(next.name);
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
