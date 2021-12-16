package aoc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc.Input.forDay;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.signum;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Day5 {
    record Line(Pos start, Pos end) {
    }

    List<Line> parseInput(Input input) {
        Pattern pattern = Pattern.compile("(?<x1>\\d+),(?<x2>\\d+) -> (?<y1>\\d+),(?<y2>\\d+)");
        return input.asListOfStrings()
                .stream()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> new Line(new Pos(
                        parseInt(matcher.group("x1")),
                        parseInt(matcher.group("x2"))),
                        (new Pos(
                                parseInt(matcher.group("y1")),
                                parseInt(matcher.group("y2"))))))
                .toList();
    }

    long solution1(Input raw) {
        return solve(raw, line -> line.start.i() == line.end.i() || line.start.j() == line.end.j());
    }

    private long solve(Input raw, Predicate<Line> filter) {
        List<Line> input = parseInput(raw);
        Map<Pos, List<Line>> map = new HashMap<>();
        input.stream()
                .filter(filter)
                .forEach(line -> {
                    int si = line.end.i() - line.start.i();
                    int di = signum(si);

                    int sj = line.end.j() - line.start.j();
                    int dj = signum(sj);

                    int length = max(abs(si), abs(sj));

                    int i = line.start.i();
                    int j = line.start.j();

                    for (int n = 0; n <= length; n++) {
                        map.computeIfAbsent(new Pos(i, j), pos -> new ArrayList<>()).add(line);
                        i += di;
                        j += dj;
                    }
                });

        return map.values().stream().filter(lines -> lines.size() > 1).count();
    }

    @Test
    void part1() {
        Input input = forDay(5);
        assertEquals(5, solution1(input.file("test1.txt")));
        assertNotEquals(905, solution1(input), "Too low");
        assertEquals(4728, solution1(input));
    }

    long solution2(Input raw) {
        return solve(raw, line -> true);
    }

    @Test
    void part2() {
        Input input = forDay(5);
        assertEquals(12, solution2(input.file("test1.txt")));
        assertEquals(17717, solution2(input));
    }
}
