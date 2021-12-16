package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Day4 {

    static class Board {
        Map<Integer, Pos> grid = new HashMap<>();
        boolean[][] matches = new boolean[5][5];

        public boolean won() {
            for (int i = 0; i < 5; i++) {
                int jn = 0;
                for (int j = 0; j < 5; j++) {
                    if (matches[i][j]) jn++;
                }
                if (jn == 5) return true;
            }

            for (int j = 0; j < 5; j++) {
                int in = 0;
                for (int i = 0; i < 5; i++) {
                    if (matches[i][j]) in++;
                }
                if (in == 5) return true;
            }

            return false;
        }

        public void addRow(int i, int[] row) {
            for (int j = 0; j < row.length; j++) {
                Pos put = grid.put(row[j], new Pos(i, j));
                if (put != null) throw new IllegalStateException();
            }
        }

        public void mark(int n) {
            Pos pos = grid.get(n);
            if (pos != null) {
                matches[pos.i()][pos.j()] = true;
            }
        }

        public int score() {
            return grid.entrySet().stream()
                    .filter(entry -> !matches[entry.getValue().i()][entry.getValue().j()])
                    .map(Map.Entry::getKey)
                    .reduce(Integer::sum)
                    .orElseThrow();
        }
    }

    static class Game {
        int position = -1;
        int[] numbers;
        List<Board> boards = new ArrayList<>();
        Stack<Board> winners = new Stack<>();

        static Game load(Input raw) {
            List<String> lines = raw.asListOfStrings();
            Game game = new Game();
            game.numbers = parseInts(",", lines.get(0));
            int i = 1;
            while (i++ < lines.size()) {
                Board board = new Board();
                board.addRow(0, parseInts("\s+", lines.get(i++)));
                board.addRow(1, parseInts("\s+", lines.get(i++)));
                board.addRow(2, parseInts("\s+", lines.get(i++)));
                board.addRow(3, parseInts("\s+", lines.get(i++)));
                board.addRow(4, parseInts("\s+", lines.get(i++)));
                game.boards.add(board);
            }
            return game;
        }

        private static int[] parseInts(String separator, String line) {
            return Arrays.stream(line.trim().split(separator)).mapToInt(Integer::parseInt).toArray();
        }

        public void play() {
            while (!isOver()) {
                move();
            }
        }

        public void play2() {
            while (position < numbers.length - 1 && !boards.isEmpty()) {
                move();
                List<Board> roundWinners = this.boards.stream().filter(Board::won).toList();
                boards.removeAll(roundWinners);
                roundWinners.forEach(winners::push);
            }
        }

        private void move() {
            int n = numbers[++position];
            boards.forEach(board -> board.mark(n));
        }

        private boolean isOver() {
            return boards.stream().anyMatch(Board::won);
        }

        Board getWinner() {
            return boards.stream().filter(Board::won).findFirst().orElseThrow();
        }
    }

    Game parseInput(Input input) {
        return Game.load(input);
    }

    int solution1(Input raw) {
        Game game = parseInput(raw);

        game.play();
        Board board = game.getWinner();

        return board.score() * game.numbers[game.position];
    }

    @Test
    void part1() {
        Input input = forDay(4);
        assertEquals(4512, solution1(input.file("test1.txt")));
        int actual = solution1(input);
        assertNotEquals(30052, actual, "Too high");
        assertEquals(11774, actual);
    }


    int solution2(Input raw) {
        Game game = parseInput(raw);

        game.play2();
        Board board = game.winners.peek();

        return board.score() * game.numbers[game.position];
    }

    @Test
    void part2() {
        Input input = forDay(4);
        assertEquals(1924, solution2(input.file("test1.txt")));
        assertEquals(4495, solution2(input));
    }
}
