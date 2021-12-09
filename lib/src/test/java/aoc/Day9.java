package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;

import static aoc.Input.forDay;
import static java.lang.Character.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day9 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(9);
        input.fetchInput();
        return input;
    }

    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    long solution1(Input raw) {
        List<String> input = parseInput(raw);

        Map<Day4.Pos, Integer> lows = getLows(input);

        return lows.values().stream().mapToInt(i -> i + 1).sum();
    }

    private Map<Day4.Pos, Integer> getLows(List<String> input) {
        Map<Day4.Pos, Integer> lows = new HashMap<>();

        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            for (int j = 0; j < line.length(); j++) {
                char top = MAX_VALUE;
                char right = MAX_VALUE;
                char bottom = MAX_VALUE;
                char left = MAX_VALUE;
                if (j > 0) left = line.charAt(j - 1);
                if (j < line.length() - 1) right = line.charAt(j + 1);
                if (i > 0) top = input.get(i - 1).charAt(j);
                if (i < input.size() - 1) bottom = input.get(i + 1).charAt(j);
                char current = line.charAt(j);
                if (current < top && current < right && current < bottom && current < left) {
                    lows.put(new Day4.Pos(i, j), Integer.parseInt("" + current));
                }
            }
        }
        return lows;
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(15, solution1(input.file("test1.txt")));
        assertEquals(508, solution1(input));
    }

    long solution2(Input raw) {
        List<String> input = parseInput(raw);

        Map<Day4.Pos, Integer> lows = getLows(input);

        List<Integer> basinSizes = new ArrayList<>();

        lows.keySet().forEach(low -> {
            Set<Day4.Pos> basin = new HashSet<>();
            explore(input, low, basin);
            basinSizes.add(basin.size());
        });

        Comparator<Integer> reversed = Comparator.<Integer>naturalOrder().reversed();

        return basinSizes.stream()
                .sorted(reversed)
                .limit(3)
                .reduce(1, (a, b) -> a * b);
    }

    private void explore(List<String> input, Day4.Pos at, Set<Day4.Pos> basin) {
        if (basin.contains(at)) return;
        basin.add(at);
        String line = input.get(at.i());
        int i = at.i();
        int j = at.j();
        char top = MAX_VALUE;
        char right = MAX_VALUE;
        char bottom = MAX_VALUE;
        char left = MAX_VALUE;
        if (j > 0) left = line.charAt(j - 1);
        if (j < line.length() - 1) right = line.charAt(j + 1);
        if (i > 0) top = input.get(i - 1).charAt(j);
        if (i < input.size() - 1) bottom = input.get(i + 1).charAt(j);
        if (top < '9') explore(input, new Day4.Pos(i - 1, j), basin);
        if (right < '9') explore(input, new Day4.Pos(i, j + 1), basin);
        if (bottom < '9') explore(input, new Day4.Pos(i + 1, j), basin);
        if (left < '9') explore(input, new Day4.Pos(i, j - 1), basin);
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(1134, solution2(input.file("test1.txt")));
        assertEquals(1564640, solution2(input));
    }
}
