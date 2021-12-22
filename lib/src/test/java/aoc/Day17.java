package aoc;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc.Input.forDay;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.naturalOrder;
import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.*;

class Day17 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(17);
        input.fetchInput();
        return input;
    }

    Area parseInput(String line) {
        Pattern pattern = compile("target area: x=(?<minX>-?\\d+)\\.\\.(?<maxX>-?\\d+), y=(?<minY>-?\\d+)\\.\\.(?<maxY>-?\\d+)");
        Matcher matcher = pattern.matcher(line);
        assertTrue(matcher.find());
        return new Area(
                new Range(parseInt(matcher.group("minX")), parseInt(matcher.group("maxX"))),
                new Range(parseInt(matcher.group("minY")), parseInt(matcher.group("maxY"))));
    }

    record Range(int min, int max) {
        public boolean contains(Range other) {
            return intersect(other).length() == other.length();
        }

        public boolean contains(int n) {
            return min() <= n && n <= max();
        }

        public Range trim(Range range) {
            return new Range(Math.max(this.min, range.min), Math.min(this.max, range.max));
        }

        public int length() {
            return max - min;
        }

        public Range intersect(Range other) {
            return new Range(Integer.max(min, other.min), Integer.min(max, other.max));
        }
    }

    record Area(Range x, Range y) {
        public boolean contains(int x, int y) {
            return x().contains(x) && y().contains(y);
        }
    }

    record Answer(int maxY, int x, int y) {
    }

    record Simulation(Area target) {

        public Integer solve1() {
            return fit().stream().map(Answer::maxY).max(naturalOrder()).orElseThrow();
        }

        public Integer solve2() {
            return fit().size();
        }

        private Set<Answer> fit() {
            Set<Answer> answers = new HashSet<>();
            for (int dx = -2000; dx < 2000; dx++) {
                for (int dy = -2000; dy < 2000; dy++) {
                    int maxY = run(dx, dy);
                    if (maxY != MIN_VALUE) {
                        answers.add(new Answer(maxY, dx, dy));
                    }
                }
            }
            return answers;
        }

        public int run(int dx, int dy) {
            int x = 0;
            int y = 0;

            int maxY = MIN_VALUE;

            while (true) {
                x += dx;
                y += dy;

                if (y > maxY) maxY = y;

                if (dx < 0) dx++;
                if (dx > 0) dx--;
                dy--;

                if (target.contains(x, y)) {
                    return maxY;
                }

                boolean goingDown = y > dy + y;
                boolean goingRight = x < dx + x;

                if ((goingDown && y < target.y().min)
                        || (goingRight && x > target.x().max)
                        || (!goingRight && x < target.x().min)) {
                    //  Missed the target
                    return MIN_VALUE;
                }
            }
        }
    }

    int solution1(Area input) {
        return new Simulation(input).solve1();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(45, solution1(parseInput("target area: x=20..30, y=-10..-5")));
        int actual = solution1(parseInput(input.asListOfStrings().get(0)));
        assertNotEquals(4950, actual, "Too low");
        assertEquals(8646, actual);
    }

    int solution2(Area input) {
        return new Simulation(input).solve2();
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(112, solution2(parseInput("target area: x=20..30, y=-10..-5")));
        assertEquals(5945, solution2(parseInput(input.asListOfStrings().get(0))));
    }
}
