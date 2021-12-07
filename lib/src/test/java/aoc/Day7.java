package aoc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.function.BiFunction;

import static aoc.Input.forDay;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Day7 {

    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(7);
        input.fetchInput();
        return input;
    }

    Map<Integer, Long> parseInput(Input input) {
        return Arrays.stream(input.asListOfStrings().get(0).split(","))
                .map(Integer::parseInt)
                .collect(groupingBy(identity(), counting()));
    }

    long solution1(Input raw, BiFunction<Map<Integer, Long>, Integer, Long> costFunction) {
        Map<Integer, Long> input = parseInput(raw);

        IntSummaryStatistics statistics = input.keySet()
                .stream()
                .mapToInt(i -> i)
                .summaryStatistics();

        int min = statistics.getMin();
        int max = statistics.getMax();

        return rangeClosed(min, max)
                .mapToLong(position -> costFunction.apply(input, position))
                .min()
                .orElseThrow();
    }

    private long cost(Map<Integer, Long> input, int position) {
        return input.entrySet()
                .stream()
                .mapToLong(entry -> Math.abs(entry.getKey() - position) * entry.getValue())
                .sum();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(37, solution1(input.file("test1.txt"), this::cost));
        assertNotEquals(450102, solution1(input, this::cost), "Too high");
        assertEquals(336120, solution1(input, this::cost));
    }

    private long cost2(Map<Integer, Long> input, int position) {
        return input.entrySet()
                .stream()
                .mapToLong(entry -> {
                    long cost = singleCost(entry.getKey(), position);
                    return cost * entry.getValue();
                })
                .sum();
    }

    private int singleCost(int a, int b) {
        long length = Math.abs(a - b);
        int cost = 0;
        for (int i = 1; i <= length; i++) {
            cost += i;
        }
        return cost;
    }

    @Test
    public void testSingleCost() {
        assertEquals(66, singleCost(5, 16));
        assertEquals(1, singleCost(1, 2));
        assertEquals(1, singleCost(0, 1));
        assertEquals(0, singleCost(1, 1));
        assertEquals(3, singleCost(4, 2));
        assertEquals(3, singleCost(0, 2));
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(168, solution1(input.file("test1.txt"), this::cost2));
        assertEquals(96864235, solution1(input, this::cost2));
    }
}
