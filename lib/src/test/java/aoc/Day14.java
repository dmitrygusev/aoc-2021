package aoc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static aoc.Input.forDay;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(14);
        input.fetchInput();
        return input;
    }

    static class Reactor {
        Map<String, AtomicLong> elementQuantities = new HashMap<>();
        Map<String, AtomicLong> polymerPairs = new HashMap<>();
        Map<String, String> reactions = new HashMap<>();

        public void produce(int n) {
            for (int i = 0; i < n; i++) {
                Map<String, AtomicLong> newGen = new HashMap<>();
                polymerPairs.forEach((pair, count) -> {
                    String first = pair.substring(0, 1);
                    String second = pair.substring(1, 2);

                    String middle = reactions.get(pair);

                    elementQuantities.computeIfAbsent(middle, key -> new AtomicLong(0)).addAndGet(count.get());

                    newGen.computeIfAbsent(first + middle, key -> new AtomicLong(0)).addAndGet(count.get());
                    newGen.computeIfAbsent(middle + second, key -> new AtomicLong(0)).addAndGet(count.get());
                });
                polymerPairs = newGen;
            }
        }
    }

    Reactor parseInput(Input input) {
        List<String> lines = input.asListOfStrings();

        Reactor reactor = new Reactor();
        String polymer = lines.get(0);
        for (int i = 0; i < polymer.length(); i++) {
            reactor.elementQuantities
                    .computeIfAbsent(polymer.substring(i, i + 1), key -> new AtomicLong(0))
                    .incrementAndGet();
        }
        for (int i = 0; i < polymer.length() - 1; i++) {
            String pair = polymer.substring(i, i + 2);
            reactor.polymerPairs
                    .computeIfAbsent(
                            pair,
                            key -> new AtomicLong(0))
                    .incrementAndGet();
        }

        for (int i = 2; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(" -> ");
            reactor.reactions.put(parts[0], parts[1]);
        }

        return reactor;
    }

    long solution1(Input raw) {
        Reactor input = parseInput(raw);

        input.produce(10);

        long max = input.elementQuantities.values().stream().max(comparing(AtomicLong::get)).orElseThrow().get();
        long min = input.elementQuantities.values().stream().min(comparing(AtomicLong::get)).orElseThrow().get();

        return max - min;
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(1588, solution1(input.file("test1.txt")));
        assertEquals(2321, solution1(input));
    }

    long solution2(Input raw) {
        Reactor input = parseInput(raw);

        input.produce(40);

        long max = input.elementQuantities.values().stream().max(comparing(AtomicLong::get)).orElseThrow().get();
        long min = input.elementQuantities.values().stream().min(comparing(AtomicLong::get)).orElseThrow().get();

        return max - min;
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(2188189693529L, solution2(input.file("test1.txt")));
        assertEquals(2399822193707L, solution2(input));
    }
}
