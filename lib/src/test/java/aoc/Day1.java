package aoc;

import org.junit.jupiter.api.Test;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day1 {

    long[] parseInput(Input input) {
        return input.asLongArray();
    }

    int solution1(Input raw) {
        long[] input = parseInput(raw);
        int result = 0;
        for (int i = 1; i < input.length; i++) {
            if (input[i] > input[i - 1]) result++;
        }
        return result;
    }

    @Test
    void part1() {
        Input input = forDay(1);
        assertEquals(7, solution1(input.file("test1.txt")));
        assertEquals(1301, solution1(input));
    }

    int solution2(Input raw) {
        long[] input = parseInput(raw);
        int result = 0;
        for (int i = 3; i < input.length; i++) {
            long previous = input[i-1] + input[i - 2] + input[i - 3];
            long current = input[i] + input[i - 1] + input[i - 2];
            if (current > previous) result++;
        }
        return result;
    }

    @Test
    void part2() {
        Input input = forDay(1);
        assertEquals(5, solution2(input.file("test1.txt")));
        assertEquals(1346, solution2(input));
    }
}
