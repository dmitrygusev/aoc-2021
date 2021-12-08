package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;

import static aoc.Input.forDay;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day8 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(8);
        input.fetchInput();
        return input;
    }

    record InOut(String[] in, String[] output) {
    }

    List<InOut> parseInput(Input input) {
        return input.asListOfStrings()
                .stream()
                .map(this::parseInOut)
                .toList();
    }

    private InOut parseInOut(String line) {
        String[] parts = line.split(" \\| ");
        return new InOut(parts[0].split(" "), parts[1].split(" "));
    }

    long solution1(Input raw) {
        List<InOut> input = parseInput(raw);

        return input.stream()
                .flatMap(inOut -> Arrays.stream(inOut.output))
                .filter(pattern -> Set.of(2, 4, 3, 7).contains(pattern.length()))
                .count();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(26, solution1(input.file("test1.txt")));
        assertEquals(381, solution1(input));
    }

    static class Display {
        Map<Character, Character> segmentWires = new HashMap<>();

        void wireOne(String wires) {
            assert wires.length() == 2;
            assert segmentWires.isEmpty();
            segmentWires.put('c', wires.charAt(0));
            segmentWires.put('f', wires.charAt(1));
        }

        void wireSeven(String wires) {
            assert wires.length() == 3;
            assert segmentWires.containsKey('c');
            assert segmentWires.containsKey('f');
            boolean aAssigned = false;
            for (char wire : wires.toCharArray()) {
                if (!segmentWires.containsValue(wire)) {
                    if (!aAssigned) {
                        segmentWires.put('a', wire);
                        aAssigned = true;
                    }
                }
            }
        }

        void wireFour(String wires) {
            assert wires.length() == 4;
            assert segmentWires.containsKey('c');
            assert segmentWires.containsKey('f');
            boolean bAssigned = false;
            boolean dAssigned = false;
            for (char wire : wires.toCharArray()) {
                if (!segmentWires.containsValue(wire)) {
                    if (!bAssigned) {
                        segmentWires.put('b', wire);
                        bAssigned = true;
                    } else if (!dAssigned) {
                        segmentWires.put('d', wire);
                        dAssigned = true;
                    }
                }
            }
        }

        boolean isNine(String wires) {
            return wires.length() == 6
                    && wires.contains(valueOf(segmentWires.get('a')))
                    && wires.contains(valueOf(segmentWires.get('b')))
                    && wires.contains(valueOf(segmentWires.get('c')))
                    && wires.contains(valueOf(segmentWires.get('d')))
                    && wires.contains(valueOf(segmentWires.get('f')));
        }

        void wireNine(String wires) {
            assert isNine(wires);
            assert segmentWires.containsKey('a');
            assert segmentWires.containsKey('b');
            assert segmentWires.containsKey('c');
            assert segmentWires.containsKey('d');
            assert segmentWires.containsKey('f');
            boolean gAssigned = false;
            for (char wire : wires.toCharArray()) {
                if (!segmentWires.containsValue(wire)) {
                    if (!gAssigned) {
                        segmentWires.put('g', wire);
                        gAssigned = true;
                    }
                }
            }
        }

        boolean isZero(String wires) {
            return wires.length() == 6
                    && wires.contains(valueOf(segmentWires.get('a')))
                    && wires.contains(valueOf(segmentWires.get('b')))
                    && wires.contains(valueOf(segmentWires.get('c')))
                    && wires.contains(valueOf(segmentWires.get('e')))
                    && wires.contains(valueOf(segmentWires.get('f')))
                    && wires.contains(valueOf(segmentWires.get('g')));
        }

        boolean isOne(String wires) {
            return wires.length() == 2;
        }

        boolean isTwo(String wires) {
            return wires.length() == 5
                    && wires.contains(valueOf(segmentWires.get('a')))
                    && wires.contains(valueOf(segmentWires.get('c')))
                    && wires.contains(valueOf(segmentWires.get('d')))
                    && wires.contains(valueOf(segmentWires.get('e')))
                    && wires.contains(valueOf(segmentWires.get('g')));
        }

        boolean isThree(String wires) {
            return wires.length() == 5
                    && wires.contains(valueOf(segmentWires.get('a')))
                    && wires.contains(valueOf(segmentWires.get('c')))
                    && wires.contains(valueOf(segmentWires.get('d')))
                    && wires.contains(valueOf(segmentWires.get('f')))
                    && wires.contains(valueOf(segmentWires.get('g')));
        }

        boolean isFour(String wires) {
            return wires.length() == 4;
        }

        boolean isFive(String wires) {
            return wires.length() == 5
                    && wires.contains(valueOf(segmentWires.get('a')))
                    && wires.contains(valueOf(segmentWires.get('b')))
                    && wires.contains(valueOf(segmentWires.get('d')))
                    && wires.contains(valueOf(segmentWires.get('f')))
                    && wires.contains(valueOf(segmentWires.get('g')));
        }

        boolean isSix(String wires) {
            return wires.length() == 6
                    && wires.contains(valueOf(segmentWires.get('a')))
                    && wires.contains(valueOf(segmentWires.get('b')))
                    && wires.contains(valueOf(segmentWires.get('d')))
                    && wires.contains(valueOf(segmentWires.get('e')))
                    && wires.contains(valueOf(segmentWires.get('f')))
                    && wires.contains(valueOf(segmentWires.get('g')));
        }

        boolean isSeven(String wires) {
            return wires.length() == 3;
        }

        boolean isEight(String wires) {
            return wires.length() == 7;
        }

        int decode(String wires) {
            if (isZero(wires)) return 0;
            if (isOne(wires)) return 1;
            if (isTwo(wires)) return 2;
            if (isThree(wires)) return 3;
            if (isFour(wires)) return 4;
            if (isFive(wires)) return 5;
            if (isSix(wires)) return 6;
            if (isSeven(wires)) return 7;
            if (isEight(wires)) return 8;
            if (isNine(wires)) return 9;
            throw new IllegalArgumentException();
        }

        public void calibrate(String[] digits) {
            for (String digit : digits) if (isOne(digit)) wireOne(digit);
            for (String digit : digits) if (isFour(digit)) wireFour(digit);
            for (String digit : digits) if (isSeven(digit)) wireSeven(digit);
            for (String digit : digits) if (isNine(digit)) wireNine(digit);
            wireEight();
            wireThree(digits);
            wireFive(digits);
            //  Self-check that all digits can be decoded after the calibration
            for (String digit : digits) decode(digit);
        }

        private void wireFive(String[] digits) {
            //  Five should be A B D F G, but may be currently wired as A B D C G,
            //  if that's the case we need to swap C <-> F

            if (Arrays.stream(digits).noneMatch(this::isFive)) {
                char oldC = segmentWires.get('c');
                segmentWires.put('c', segmentWires.get('f'));
                segmentWires.put('f', oldC);
            }

            assert Arrays.stream(digits).anyMatch(this::isFive);
        }

        private void wireThree(String[] digits) {
            //  Three should be A C D F G, but may be currently wired as A C B F G,
            //  if that's the case we need to swap D <-> B

            if (Arrays.stream(digits).noneMatch(this::isThree)) {
                char oldB = segmentWires.get('b');
                segmentWires.put('b', segmentWires.get('d'));
                segmentWires.put('d', oldB);
            }

            assert Arrays.stream(digits).anyMatch(this::isThree);
        }

        private void wireEight() {
            Set<Character> segments = new HashSet<>(Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g'));
            segmentWires.values().forEach(segments::remove);
            assert segments.size() == 1;
            segmentWires.put('e', segments.iterator().next());
        }
    }

    @Test
    void testValidDisplay() {
        InOut inOut = parseInOut("abcefg cf acdeg acdfg bcdf abdfg abdefg acf abcdefg abcdfg | abcefg cf acdeg acdfg bcdf abdfg abdefg acf abcdefg abcdfg");
        Display display = new Display();
        display.calibrate(inOut.in);
        String result = Arrays.stream(inOut.output)
                .mapToInt(display::decode)
                .mapToObj(String::valueOf)
                .collect(joining());
        assertEquals("0123456789", result);
    }

    @Test
    void testDisplay() {
        InOut inOut = parseInOut("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf");
        Display display = new Display();
        display.calibrate(inOut.in);
        String result = Arrays.stream(inOut.output)
                .mapToInt(display::decode)
                .mapToObj(String::valueOf)
                .collect(joining());
        assertEquals("5353", result);
    }

    @Test
    void testDisplay2() {
        InOut inOut = parseInOut("be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe");
        Display display = new Display();
        display.calibrate(inOut.in);
        String result = Arrays.stream(inOut.output)
                .mapToInt(display::decode)
                .mapToObj(String::valueOf)
                .collect(joining());
        assertEquals("8394", result);
    }

    int solution2(Input raw) {
        List<InOut> input = parseInput(raw);

        return input.stream().mapToInt(inOut -> {
                    String[] in = inOut.in;
                    Display display = new Display();
                    display.calibrate(in);

                    String result = Arrays.stream(inOut.output)
                            .map(display::decode)
                            .map(String::valueOf)
                            .collect(joining());

                    return Integer.parseInt(result);

                })
                .sum();
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(61229, solution2(input.file("test1.txt")));
        assertEquals(1023686, solution2(input));
    }
}
