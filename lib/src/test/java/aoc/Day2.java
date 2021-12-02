package aoc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day2 {
    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    long solution1(Input raw) {
        List<String> input = parseInput(raw);

        int h = 0;
        int depth = 0;

        for (String command : input) {
            String[] parts = command.split(" ");
            final long x = Long.parseLong(parts[1]);
            switch (parts[0]) {
                case "forward" -> h += x;
                case "up" -> depth -= x;
                case "down" -> depth += x;
            }
        }

        return (long) h * depth;
    }

    @Test
    void part1() {
        Input input = forDay(2);
        assertEquals(150, solution1(input.file("test1.txt")));
        assertEquals(1813801, solution1(input));
    }

    long solution2(Input raw) {
        List<String> input = parseInput(raw);

        int h = 0;
        int depth = 0;
        int aim = 0;

        for (String command : input) {
            String[] parts = command.split(" ");
            final long x = Long.parseLong(parts[1]);
            switch (parts[0]) {
                case "forward" -> {
                    h += x;
                    depth += aim * x;
                }
                case "up" -> aim -= x;
                case "down" -> aim += x;
            }
        }

        return (long) h * depth;
    }

    @Test
    void part2() {
        Input input = forDay(2);
        assertEquals(900, solution2(input.file("test1.txt")));
        assertEquals(1960569556, solution2(input));
    }
}
