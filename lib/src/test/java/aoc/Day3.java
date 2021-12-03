package aoc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import static aoc.Input.forDay;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day3 {
    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    int solution1(Input raw) {
        List<String> input = parseInput(raw);

        int[] ones = countOnes(input);

        String gammaBits = IntStream.of(ones).mapToObj(n -> n > input.size() / 2 ? "1" : "0").collect(joining());
        int gamma = Integer.parseInt(gammaBits, 2);

        String epsilonBits = gammaBits.chars()
                .mapToObj(ch -> ch == '0' ? "1" : "0")
                .collect(joining());
        int epsilon = Integer.parseInt(epsilonBits, 2);

        return gamma * epsilon;
    }

    private int[] countOnes(List<String> input) {
        int nBits = input.get(0).length();

        int[] ones = new int[nBits];

        input.forEach(line -> {
            char[] chars = line.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '1') ones[i]++;
            }
        });
        return ones;
    }

    @Test
    void part1() {
        Input input = forDay(3);
        assertEquals(198, solution1(input.file("test1.txt")));
        assertEquals(4006064, solution1(input));
    }

    String filter(List<String> input, boolean keepMostCommon) {
        List<String> filtered = new ArrayList<>(input);
        outer:
        for (int i = 0; i < filtered.get(0).length(); i++) {
            int[] ones = countOnes(filtered);
            char commonBit = ones[i] >= filtered.size() / 2.0 ? '1' : '0';
            Iterator<String> iterator = filtered.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if ((keepMostCommon && line.charAt(i) != commonBit)
                        || (!keepMostCommon && line.charAt(i) == commonBit)) {
                    iterator.remove();
                }
                if (filtered.size() == 1) {
                    break outer;
                }
            }
        }
        return filtered.get(0);
    }

    int solution2(Input raw) {
        List<String> input = parseInput(raw);

        String oxygenBits = filter(input, true);
        String co2Bits = filter(input, false);

        int oxygen = Integer.parseInt(oxygenBits, 2);
        int co2 = Integer.parseInt(co2Bits, 2);

        return oxygen * co2;
    }

    @Test
    void part2() {
        Input input = forDay(3);
        assertEquals(230, solution2(input.file("test1.txt")));
        assertEquals(5941884, solution2(input));
    }
}
