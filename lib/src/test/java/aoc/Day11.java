package aoc;

import aoc.Day4.Pos;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static aoc.Input.forDay;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(11);
        input.fetchInput();
        return input;
    }

    static class Field {
        Map<Pos, AtomicInteger> map = new HashMap<>();
        Set<Pos> flashed = new HashSet<>();

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int value = map.get(new Pos(i, j)).get();
                    builder.append(value >= 10 ? "*" : value);
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public int step() {
            increment();
            recharge();
            int count = flashed.size();
            flashed.clear();
            return count;
        }

        private void increment() {
            map.keySet().forEach(this::increment);
        }

        private void increment(Pos pos) {
            if (map.get(pos).incrementAndGet() > 9) {
                flash(pos);
            }
        }

        private void recharge() {
            flashed.forEach(pos -> map.get(pos).set(0));
        }

        private void flash(Pos pos) {
            if (flashed.contains(pos)) return;

            flashed.add(pos);

            pos.adjacentWithDiagonals().forEach(adjacent ->
                    Optional.of(adjacent)
                            .filter(map::containsKey)
                            .ifPresent(this::increment));
        }
    }

    Field parseInput(Input input) {
        Field field = new Field();
        List<String> lines = input.asListOfStrings();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                field.map.put(
                        new Pos(i, j),
                        new AtomicInteger(parseInt(valueOf(line.charAt(j)))));
            }
        }

        return field;
    }

    int solution1(Input raw) {
        Field input = parseInput(raw);

        int count = 0;
        for (int i = 0; i < 100; i++) {
            count += input.step();
        }

        return count;
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(1656, solution1(input.file("test1.txt")));
        assertEquals(1719, solution1(input));
    }

    int solution2(Input raw) {
        Field input = parseInput(raw);

        int step = 1;
        while (true) {
            int count = input.step();
            if (count == 100) return step;
            step++;
        }
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(195, solution2(input.file("test1.txt")));
        assertEquals(232, solution2(input));
    }
}
