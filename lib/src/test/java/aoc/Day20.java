package aoc;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static aoc.Input.forDay;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Day20 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(20);
        input.fetchInput();
        return input;
    }

    static class Field {
        final String algorithm;
        Set<Pos> marks = new HashSet<>();

        int minI;
        int maxI;
        int minJ;
        int maxJ;

        Field(String algorithm) {
            this.algorithm = algorithm;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = minI; i <= maxI; i++) {
                for (int j = minJ; j <= maxJ; j++) {
                    builder.append(marks.contains(new Pos(i, j)) ? "#" : " ");
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public Field readMarks(List<String> input) {
            for (int i = 0; i < input.size(); i++) {
                String line = input.get(i);
                for (int j = 0; j < line.length(); j++) {
                    char ch = line.charAt(j);
                    if (ch == '#') {
                        marks.add(new Pos(i, j));
                        if (minI > i) minI = i;
                        if (maxI < i) maxI = i;
                        if (minJ > j) minJ = j;
                        if (maxJ > j) maxJ = j;
                    }
                }
            }
            return this;
        }

        public void iterate(int n, int kernelCutOff, int kernelExpand) {
            for (int k = 1; k <= n; k++) {
//                System.out.println(k + " iteration, " + marks.size() + " marks");

                Set<Pos> newMarks = new HashSet<>();

                minI = marks.stream().mapToInt(Pos::i).min().orElseThrow() - kernelExpand;
                maxI = marks.stream().mapToInt(Pos::i).max().orElseThrow() + kernelExpand;
                minJ = marks.stream().mapToInt(Pos::j).min().orElseThrow() - kernelExpand;
                maxJ = marks.stream().mapToInt(Pos::j).max().orElseThrow() + kernelExpand;

                for (int i = minI; i <= maxI; i++) {
                    for (int j = minJ; j <= maxJ; j++) {
                        Pos pos = new Pos(i, j);

                        String bits = pos.surrounding3x3Ordered()
                                .stream()
                                .map(mark -> marks.contains(mark))
                                .map(present -> present ? "1" : "0")
                                .collect(joining());

                        int index = parseInt(bits, 2);

                        if (algorithm.charAt(index) == '#') {
                            newMarks.add(pos);
                        }
                    }
                }

                int iterationCutOff = k % 2 == 0 ? kernelCutOff : 0;

                this.marks = newMarks.stream()
                        .filter(pos -> pos.within(
                                minI + iterationCutOff,
                                maxI - iterationCutOff,
                                minJ + iterationCutOff,
                                maxJ - iterationCutOff))
                        .collect(toSet());
            }
        }
    }

    Field parseInput(Input input) {
        List<String> lines = input.asListOfStrings();
        return new Field(lines.get(0)).readMarks(lines.subList(2, lines.size()));
    }

    int solve(Input raw, int n, int kernelExpand, int kernelCutOff) {
        Field input = parseInput(raw);

        input.iterate(n, kernelCutOff, kernelExpand);

//        System.out.println(input);

        return input.marks.size();
    }

    @Test
    void part1() {
        Input input = getInput();
        Input test1 = input.file("test1.txt");

        //  Magic numbers defining image kernel for both inputs
        int testExpand = 1;
        int testCutOff = 0;

        int realExpand = 3;
        int realCutOff = 4;

        int actualPart1Test1 = solve(test1, 2, testExpand, testCutOff);
        assertEquals(35, actualPart1Test1);

        int actualPart1 = solve(input, 2, realExpand, realCutOff);
        assertNotEquals(6754, actualPart1, "Too high");
        assertNotEquals(6109, actualPart1, "Too high");
        assertNotEquals(5431, actualPart1, "Too high");
        assertEquals(5347, actualPart1);

        //  Part 2

        int actualPart2Test1 = solve(test1, 50, testExpand, testCutOff);
        assertEquals(3351, actualPart2Test1);

        int actualPart2 = solve(input, 50, realExpand, realCutOff);
        assertEquals(17172, actualPart2);
    }
}
