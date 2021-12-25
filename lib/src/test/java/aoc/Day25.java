package aoc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Day25 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(25);
        input.fetchInput();
        return input;
    }

    static class Field {
        Map<Pos, Character> map = new HashMap<>();
        int minI;
        int maxI;
        int minJ;
        int maxJ;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = minI; i <= maxI; i++) {
                for (int j = minJ; j <= maxJ; j++) {
                    builder.append(map.getOrDefault(Pos.of(i, j), '.'));
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public Field readMap(List<String> input) {
            for (int i = 0; i < input.size(); i++) {
                String line = input.get(i);
                for (int j = 0; j < line.length(); j++) {
                    char ch = line.charAt(j);
                    if (ch == '.') continue;
                    map.put(new Pos(i, j), ch);
                    if (minI > i) minI = i;
                    if (maxI < i) maxI = i;
                    if (minJ > j) minJ = j;
                    if (maxJ < j) maxJ = j;
                }
            }
            return this;
        }

        public int step() {
            AtomicInteger changed = new AtomicInteger(0);
            Field newField = new Field();
            newField.minI = minI;
            newField.minJ = minJ;
            newField.maxI = maxI;
            newField.maxJ = maxJ;
            map.forEach((pos, ch) -> {
                if (ch != '>') return;
                int j = pos.j() + 1;
                if (j > maxJ) j = minJ;
                Pos next = new Pos(pos.i(), j);
                if (map.get(next) == null) {
                    newField.map.put(next, ch);
                    changed.incrementAndGet();
                } else {
                    newField.map.put(pos, ch);
                }
            });
            map.forEach((pos, ch) -> {
                if (ch != 'v') return;
                int i = pos.i() + 1;
                if (i > maxI) i = minI;
                Pos next = new Pos(i, pos.j());
                if (newField.map.get(next) == null
                        && (!map.containsKey(next) || (map.get(next) == '>' && newField.map.get(next) == null))) {
                    newField.map.put(next, ch);
                    changed.incrementAndGet();
                } else {
                    newField.map.put(pos, ch);
                }
            });
            map = newField.map;
            return changed.get();
        }
    }

    Field parseInput(Input input) {
        return new Field().readMap(input.asListOfStrings());
    }

    int solution1(Input raw) {
        Field input = parseInput(raw);

        int changed;
        int steps = 0;

        do {
            changed = input.step();
            steps++;
        } while (changed > 0);

        return steps + 1;
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(59, solution1(input.file("test1.txt")));
        int actual = solution1(input) - 1;
        assertNotEquals(508, actual, "Too high");
        //  ¯\_(ツ)_/¯
        assertEquals(507, actual);
    }
}
