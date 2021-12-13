package aoc;

import aoc.Day4.Pos;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Input.forDay;
import static java.lang.Character.isDigit;
import static java.lang.Integer.*;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Day13 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(13);
        input.fetchInput();
        return input;
    }

    class Field {
        Set<Pos> marks = new HashSet<>();

        public String toString() {
            StringBuilder builder = new StringBuilder();
            int minI = marks.stream().mapToInt(Pos::i).min().orElseThrow();
            int maxI = marks.stream().mapToInt(Pos::i).max().orElseThrow();
            int minJ = marks.stream().mapToInt(Pos::j).min().orElseThrow();
            int maxJ = marks.stream().mapToInt(Pos::j).max().orElseThrow();
            for (int i = minI; i <= maxI; i++) {
                for (int j = minJ; j <= maxJ; j++) {
                    builder.append(marks.contains(new Pos(i, j)) ? "#" : " ");
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        Pattern pattern = Pattern.compile("fold along (?<xy>y|x)=(?<n>\\d+)");

        public void fold(String instruction) {
            Matcher matcher = pattern.matcher(instruction);
            assertTrue(matcher.find());
            switch (matcher.group("xy")) {
                case "x" -> foldJ(parseInt(matcher.group("n")));
                case "y" -> foldI(parseInt(matcher.group("n")));
                default -> throw new IllegalStateException("Unexpected value: " + matcher.group("xy"));
            }
        }

        private void foldI(int i) {
            Set<Pos> toFlip = marks.stream()
                    .filter(pos -> pos.i() > i)
                    .collect(Collectors.toSet());

            Set<Pos> flipped = toFlip.stream()
                    .map(pos -> new Pos(i - (pos.i() - i), pos.j()))
                    .collect(Collectors.toSet());

            marks.removeAll(toFlip);
            marks.addAll(flipped);
        }

        private void foldJ(int j) {
            Set<Pos> toFlip = marks.stream()
                    .filter(pos -> pos.j() > j)
                    .collect(Collectors.toSet());

            Set<Pos> flipped = toFlip.stream()
                    .map(pos -> new Pos(pos.i(), j - (pos.j() - j)))
                    .collect(Collectors.toSet());

            marks.removeAll(toFlip);
            marks.addAll(flipped);
        }

        public void readMarks(List<String> input) {
            input.stream()
                    .filter(not(String::isEmpty))
                    .filter(line -> isDigit(line.charAt(0)))
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        marks.add(new Pos(parseInt(parts[1]), parseInt(parts[0])));
                    });
        }
    }

    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    int solution1(Input raw) {
        List<String> input = parseInput(raw);

        Field field = new Field();
        field.readMarks(input);

        field.fold(input.get(field.marks.size() + 1));

        return field.marks.size();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(17, solution1(input.file("test1.txt")));
        assertEquals(682, solution1(input));
    }

    Field solution2(Input raw) {
        List<String> input = parseInput(raw);

        Field field = new Field();
        field.readMarks(input);

        input.stream()
                .filter(line -> line.startsWith("fold"))
                .forEach(field::fold);

        return field;
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals("""
                #####
                #   #
                #   #
                #   #
                #####
                """,
                solution2(input.file("test1.txt")).toString());

        assertEquals("""
                ####  ##   ##  #  # ###  #### #  # ####
                #    #  # #  # #  # #  #    # #  # #\040\040\040
                ###  #  # #    #  # #  #   #  #### ###\040
                #    #### # ## #  # ###   #   #  # #\040\040\040
                #    #  # #  # #  # # #  #    #  # #\040\040\040
                #    #  #  ###  ##  #  # #### #  # ####
                """, solution2(input).toString());
    }
}
