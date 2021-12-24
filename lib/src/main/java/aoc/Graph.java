package aoc;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Graph<N, W> {
    Map<N, List<Pair<N, W>>> connections = new HashMap<>();

    public void addEdge(N a, N b) {
        addEdge(a, b, null);
    }

    protected abstract void addEdge(N a, N b, W weight);

    //  Spanning trees
    public Set<List<N>> dfs(N start, N end, BiFunction<Stack<N>, N, Boolean> canVisit) {
        HashSet<List<N>> allPaths = new HashSet<>();
        dfs(new Stack<>(), start, end, allPaths, canVisit);
        return allPaths;
    }

    private void dfs(
            Stack<N> path,
            N next,
            N end,
            Set<List<N>> paths,
            BiFunction<Stack<N>, N, Boolean> canVisit) {

        if (!canVisit.apply(path, next)) return;

        path.push(next);
        try {
            if (next.equals(end)) {
                paths.add(List.copyOf(path));
                return;
            }
            List<Pair<N, W>> adjacent = connections.get(next);
            adjacent.forEach(target -> dfs(path, target.key(), end, paths, canVisit));
        } finally {
            path.pop();
        }
    }

    public Map<N, Long> dijkstraLong(N first) {
        return dijkstra(first, value -> ((Number) value).longValue(), Long::sum, 0L);
    }

    public <T extends Comparable<T>> Map<N, T> dijkstra(N first, Function<W, T> weight, BiFunction<T, T, T> addition, T zero) {
        Map<N, T> result = new HashMap<>();

        result.put(first, zero);

        Set<N> visited = new HashSet<>();
        Map<N, T> toVisit = new HashMap<>();

        N current = first;

        do {
            for (Pair<N, W> edge : connections.getOrDefault(current, List.of())) {
                T value = addition.apply(result.get(current), weight.apply(edge.value()));

                result.compute(edge.key(), (node, nodeValue) -> {
                    if (nodeValue == null) return value;
                    return nodeValue.compareTo(value) < 0 ? nodeValue : value;
                });

                if (!visited.contains(edge.key())) {
                    toVisit.put(edge.key(), result.get(edge.key()));
                }
            }

            visited.add(current);
            toVisit.remove(current);

            current = toVisit.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

        } while (current != null);

        return result;
    }

    public static class UndirectedGraph<N, W> extends Graph<N, W> {

        @Override
        public void addEdge(N a, N b, W weight) {
            connections.computeIfAbsent(a, k -> new ArrayList<>()).add(new Pair<>(b, weight));
            connections.computeIfAbsent(b, k -> new ArrayList<>()).add(new Pair<>(a, weight));
        }
    }

    public static class DirectedGraph<N, W> extends Graph<N, W> {

        @Override
        public void addEdge(N a, N b, W weight) {
            connections.computeIfAbsent(a, k -> new ArrayList<>()).add(new Pair<>(b, weight));
        }
    }
}
