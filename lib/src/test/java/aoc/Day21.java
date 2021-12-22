package aoc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day21 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(21);
        input.fetchInput();
        return input;
    }

    interface Dice {
        int roll();
    }

    class DeterministicDice implements Dice {
        int value = 1;

        @Override
        public int roll() {
            return value++;
        }
    }

    class Player {
        String id;
        int pos;
        int score;

        public Player(String id, int pos) {
            this.id = id;
            this.pos = pos - 1;
            this.score = 0;
        }

        @Override
        public String toString() {
            return "Player{" +
                    "id='" + id + '\'' +
                    ", pos=" + pos +
                    ", score=" + score +
                    '}';
        }
    }

    class Game {
        Player[] players;
        int currentPlayer;
        int totalRounds;

        final Dice dice;

        Game(Dice dice, int player1Pos, int player2Pos) {
            this.dice = dice;
            this.players = new Player[]{
                    new Player("1", player1Pos),
                    new Player("2", player2Pos)
            };
        }

        void round() {
            int sum = (dice.roll() + dice.roll() + dice.roll());
            Player player = players[currentPlayer];
            int newPos = (player.pos + sum) % 10;
            player.pos = newPos;
            player.score += newPos + 1;
            currentPlayer = 1 - currentPlayer;
            totalRounds++;
        }

        int timesDiceRolled() {
            return totalRounds * 3;
        }

        boolean isOver() {
            return players[0].score >= 1000 || players[1].score >= 1000;
        }

        public Player loser() {
            return players[0].score < players[1].score ? players[0] : players[1];
        }
    }

    long solution1(Game game) {
        while (!game.isOver()) game.round();

        return (long) game.loser().score * game.timesDiceRolled();
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(739785, solution1(new Game(new DeterministicDice(), 4, 8)));
        assertEquals(908595, solution1(new Game(new DeterministicDice(), 4, 2)));
    }

    record GameState(int p1Pos, int p1Score, int p2Pos, int p2Score) {
        public GameState move(int player, int distance) {
            if (player == 0) {
                int newPos = (p1Pos + distance) % 10;
                return new GameState(newPos, p1Score + newPos + 1, p2Pos, p2Score);
            }

            int newPos = (p2Pos + distance) % 10;
            return new GameState(p1Pos, p1Score, newPos, p2Score + newPos + 1);
        }

        public boolean isGameOver() {
            return p1Score >= 21 || p2Score >= 21;
        }
    }

    @SuppressWarnings("SameParameterValue")
    long solution2(int p1pos, int p2pos) {
        Map<GameState, Long> quantumState = new HashMap<>();

        GameState start = new GameState(p1pos - 1, 0, p2pos - 1, 0);
        quantumState.put(start, 1L);

        long p1Wins = 0;
        long p2Wins = 0;

        final int[] multipliers = {1, 3, 6, 7, 6, 3, 1};    //  sum = 27

        int currentPlayer = 0;
        while (true) {
            Map<GameState, Long> nextQS = new HashMap<>();
            for (Entry<GameState, Long> stateCount : quantumState.entrySet()) {
                for (int i = 0; i < 7; i++) {
                    int distance = i + 3;
                    int multiplier = multipliers[i];

                    GameState state = stateCount.getKey();
                    Long oldCount = stateCount.getValue();

                    GameState stateAfterNextMove = state.move(currentPlayer, distance);

                    long newCount = oldCount * multiplier;

                    if (stateAfterNextMove.isGameOver()) {
                        if (currentPlayer == 0) {
                            p1Wins += newCount;
                        } else {
                            p2Wins += newCount;
                        }
                    } else {
                        nextQS.merge(stateAfterNextMove, newCount, Long::sum);
                    }
                }
            }

            if (nextQS.isEmpty()) {
                break;
            }

            currentPlayer = 1 - currentPlayer;
            quantumState = nextQS;
        }

        return Math.max(p1Wins, p2Wins);
    }

    @Test
    void part2() {
        assertEquals(444356092776315L, solution2(4, 8));
        assertEquals(91559198282731L, solution2(4, 2));
    }
}
