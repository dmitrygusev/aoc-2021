package aoc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day6 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(6);
        input.fetchInput();
        return input;
    }

    long[] parseInput(Input input) {
        final long[] state = new long[9];
        Arrays.stream(input.asListOfStrings().get(0)
                .split(","))
                .mapToInt(Integer::parseInt)
                .forEach(n -> state[n]++);
        return state;
    }

    long solution1(Input raw, int n) {
        long[] input = parseInput(raw);

        for (int d = 0; d < n; d++) {
            long[] newState = new long[9];
            for (int i = 0; i < 9; i++) {
                newState[i] = input[(i + 1) % 9];
            }
            newState[6] += input[0];
            input = newState;
        }

        return Arrays.stream(input).sum();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(26, solution1(input.file("test1.txt"), 18));
        assertEquals(5934, solution1(input.file("test1.txt"), 80));
        assertEquals(353079, solution1(input, 80));

        //  Part 2
        assertEquals(26984457539L, solution1(input.file("test1.txt"), 256));
        assertEquals(1605400130036L, solution1(input, 256));
    }
}
