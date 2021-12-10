package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(10);
        input.fetchInput();
        return input;
    }

    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    Map<Character, Character> brackets =
            Map.of(
                    '[', ']',
                    '(', ')',
                    '<', '>',
                    '{', '}');

    long solution1(Input raw) {
        List<String> input = parseInput(raw);

        long score = 0;
        for (String line : input) {
            Stack<Character> stack = new Stack<>();
            for (char current : line.toCharArray()) {
                if (brackets.containsKey(current)) {
                    //  Opening
                    stack.push(current);
                } else {
                    //  Closing
                    char opening = stack.pop();
                    if (brackets.get(opening) != current) {
                        //  Corrupted
                        score += switch (current) {
                            case ')' -> 3;
                            case ']' -> 57;
                            case '}' -> 1197;
                            case '>' -> 25137;
                            default -> throw new IllegalStateException("Unexpected value: " + current);
                        };
                        break;
                    }
                }
            }
        }
        return score;
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(26397, solution1(input.file("test1.txt")));
        assertEquals(462693, solution1(input));
    }

    long solution2(Input raw) {
        List<String> input = parseInput(raw);

        List<Long> totalScores = new ArrayList<>();
        nextLine:
        for (String line : input) {
            long totalScore = 0;
            Stack<Character> stack = new Stack<>();
            for (char current : line.toCharArray()) {
                if (brackets.containsKey(current)) {
                    //  Opening
                    stack.push(current);
                } else {
                    //  Closing
                    char opening = stack.pop();
                    if (brackets.get(opening) != current) {
                        //  Corrupted
                        continue nextLine;
                    }
                }
            }
            while (!stack.isEmpty()) {
                totalScore *= 5;
                char closing = brackets.get(stack.pop());
                totalScore += switch (closing) {
                    case ')' -> 1;
                    case ']' -> 2;
                    case '}' -> 3;
                    case '>' -> 4;
                    default -> throw new IllegalStateException("Unexpected value: " + closing);
                };
            }
            totalScores.add(totalScore);
        }
        totalScores.sort(Comparator.naturalOrder());
        return totalScores.get(totalScores.size() / 2);
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(288957, solution2(input.file("test1.txt")));
        assertEquals(3094671161L, solution2(input));
    }
}
