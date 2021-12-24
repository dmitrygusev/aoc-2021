package aoc;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static aoc.Day23.Burrow.*;
import static aoc.Day23.Species.*;
import static aoc.Input.forDay;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day23 {

    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(23);
        input.fetchInput();
        return input;
    }

    enum Species {
        A(1), B(10), C(100), D(1000), Null(0);

        final int cost;

        Species(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            return switch (this) {
                case A, B, C, D -> name();
                default -> " ";
            };
        }
    }

    static class Burrow {
        int moves;
        final int cost;
        final Map<Pos, Species> map = new HashMap<>(8);
        final Map<Species, List<Pos>> homeRoomsFor;

        Burrow(int cost, Map<Species, List<Pos>> homeRoomsFor) {
            this.cost = cost;
            this.homeRoomsFor = homeRoomsFor;
        }

        public int cost() {
            return cost;
        }

        public int moves() {
            return moves;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            int minI = map.keySet().stream().mapToInt(Pos::i).min().orElseThrow();
            int maxI = map.keySet().stream().mapToInt(Pos::i).max().orElseThrow();
            int minJ = map.keySet().stream().mapToInt(Pos::j).min().orElseThrow();
            int maxJ = map.keySet().stream().mapToInt(Pos::j).max().orElseThrow();
            for (int i = minI; i <= maxI; i++) {
                for (int j = minJ; j <= maxJ; j++) {
                    builder.append(map.getOrDefault(Pos.of(i, j), Null).toString());
                }
                if (i == 0) {
                    builder.append(" cost=").append(cost).append(", moves=").append(moves);
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        /*
            (1,1) (1,2) (1,3) (1,4) (1,5) (1,6) (1,7) (1,8) (1,9) (1,10) (1,11)
                        (2,3)       (2,5)       (2,7)       (2,9)
                        (3,3)       (3,5)       (3,7)       (3,9)
                          A           B           C           D
         */

        final static List<Pos> hallwayStops = List.of(
                Pos.of(1, 1),
                Pos.of(1, 2),
                Pos.of(1, 4),
                Pos.of(1, 6),
                Pos.of(1, 8),
                Pos.of(1, 10),
                Pos.of(1, 11));

        final static Map<Species, List<Pos>> homeRoomsForPart1 = Map.of(
                A, List.of(Pos.of(2, 3), Pos.of(3, 3)),
                B, List.of(Pos.of(2, 5), Pos.of(3, 5)),
                C, List.of(Pos.of(2, 7), Pos.of(3, 7)),
                D, List.of(Pos.of(2, 9), Pos.of(3, 9)));

        final static Map<Species, List<Pos>> homeRoomsForPart2 = Map.of(
                A, List.of(Pos.of(2, 3), Pos.of(3, 3), Pos.of(4, 3), Pos.of(5, 3)),
                B, List.of(Pos.of(2, 5), Pos.of(3, 5), Pos.of(4, 5), Pos.of(5, 5)),
                C, List.of(Pos.of(2, 7), Pos.of(3, 7), Pos.of(4, 7), Pos.of(5, 7)),
                D, List.of(Pos.of(2, 9), Pos.of(3, 9), Pos.of(4, 9), Pos.of(5, 9)));

        public static List<Pos> path(Pos from, Pos to) {
            List<Pos> result = new ArrayList<>();
            int j = from.j();
            int dj = (int) Math.signum(to.j() - from.j());
            if (from.i() < to.i()) {
                // going down -- horizontal segment first
                do {
                    result.add(Pos.of(from.i(), j));
                    j += dj;
                } while (j != to.j());
                for (int i = from.i(); i <= to.i(); i++) result.add(Pos.of(i, to.j()));
            } else {
                //  going up -- vertical segment first
                for (int i = from.i(); i >= to.i(); i--) result.add(Pos.of(i, from.j()));
                do {
                    j += dj;
                    result.add(Pos.of(to.i(), j));
                } while (j != to.j());
            }
            return result;
        }

        public Collection<Pos> possibleTargets(Pos from) {
            Species who = map.get(from);
            List<Pos> homes = homeRoomsFor.get(who);
            if (from.i() > 1) {
                //  Can only move to hallway, anyone can move there
                //  Unless they're already home
                boolean homeColumn = homes.get(0).j() == from.j();
                if (!homeColumn) {
                    return hallwayStops;
                }
                for (int i = homes.size() - 1; i >= 0; i--) {
                    Pos pos = homes.get(i);
                    Species occupiedBy = map.get(pos);
                    if (occupiedBy != null && occupiedBy != who) {
                        //  Blocks other species, need to move out
                        return hallwayStops;
                    }
                }
                // Already home, no need to leave
                return Set.of();
            } else if (from.i() == 1) {
                //  Can only move to own home, its deepest flat
                //  unless it's occupied by other species
                return deepestUnoccupied(homes, who).map(Set::of).orElseGet(Set::of);
            }

            throw new IllegalArgumentException();
        }

        private Optional<Pos> deepestUnoccupied(List<Pos> homes, Species species) {
            for (int i = homes.size() - 1; i >= 0; i--) {
                Pos pos = homes.get(i);
                Species occupiedBy = map.get(pos);
                if (occupiedBy == null) {
                    return Optional.of(pos);
                }
                if (occupiedBy != species) {
                    return empty();
                }
            }
            throw new IllegalStateException("Should never reach this place");
        }

        public Set<Pos> tenants() {
            return map.entrySet().stream()
                    .filter(entry -> entry.getValue().cost > 0)
                    .map(Map.Entry::getKey)
                    .collect(toSet());
        }

        public Burrow move(Pos from, Pos to) {
            int newCost = map.get(from).cost * from.manhattanDistanceTo(to);
            Burrow burrow = new Burrow(cost + newCost, homeRoomsFor);
            burrow.map.putAll(map);
            Species species = burrow.map.remove(from);
            burrow.map.put(to, species);
            burrow.moves = moves + 1;
            return burrow;
        }

        public boolean isSettled() {
            return isSettled(A) && isSettled(B) && isSettled(C) && isSettled(D);
        }

        private boolean isSettled(Species species) {
            for (Pos home : homeRoomsFor.get(species)) {
                if (map.get(home) != species) {
                    return false;
                }
            }
            return true;
        }
    }

    @Test
    void testPath() {
        assertEquals(
                List.of(Pos.of(1, 2), Pos.of(1, 3), Pos.of(2, 3)),
                path(Pos.of(1, 2), Pos.of(2, 3)));

        assertEquals(
                List.of(Pos.of(1, 4), Pos.of(1, 3), Pos.of(2, 3)),
                path(Pos.of(1, 4), Pos.of(2, 3)));

        assertEquals(
                List.of(Pos.of(2, 3), Pos.of(1, 3), Pos.of(1, 2)),
                path(Pos.of(2, 3), Pos.of(1, 2)));

        assertEquals(
                List.of(Pos.of(2, 3), Pos.of(1, 3), Pos.of(1, 4)),
                path(Pos.of(2, 3), Pos.of(1, 4)));
    }

    Burrow parseInput(List<String> lines, Map<Species, List<Pos>> homeRoomsFor) {
        Burrow burrow = new Burrow(0, homeRoomsFor);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                char ch = line.charAt(j);
                switch (ch) {
                    case 'A', 'B', 'C', 'D' -> burrow.map.put(Pos.of(i, j), Species.valueOf(Character.toString(ch)));
                }
            }
        }
        return burrow;
    }

    long solution1(Input raw) {
        Burrow burrow = parseInput(raw.asListOfStrings(), homeRoomsForPart1);

        Set<Burrow> burrows = explore(burrow);

        return burrows.stream()
                .min(comparing(Burrow::cost))
                .map(Burrow::cost)
                .orElseThrow();
    }

    private Set<Burrow> explore(Burrow initial) {
        Set<Burrow> result = new HashSet<>();

        long minCompleted = Long.MAX_VALUE;

        int iteration = 0;

        PriorityQueue<Burrow> toExplore = new PriorityQueue<>(
                comparing(Burrow::moves)
                        .reversed()
                        .thenComparing(Burrow::cost));

        toExplore.offer(initial);

        do {
            Burrow burrow = toExplore.poll();
            assert burrow != null;

            iteration++;

            if (burrow.cost > minCompleted) {
                continue;
            }

            Set<Pos> tenants = burrow.tenants();

            if (burrow.isSettled()) {
                result.add(burrow);
                if (burrow.cost < minCompleted) {
                    minCompleted = burrow.cost;
                }
                continue;
            }

            for (Pos from : tenants) {
                for (Pos to : burrow.possibleTargets(from)) {
                    if (burrow.map.get(to) != null) {
                        //  Quick optimisation
                        continue;
                    }

                    List<Pos> path = path(from, to);

                    boolean pathIsClear = path.stream().skip(1).allMatch(pos -> burrow.map.get(pos) == null);

                    if (!pathIsClear) {
                        continue;
                    }

                    Burrow moved = burrow.move(from, to);

                    if (moved.cost < minCompleted) {
                        toExplore.offer(moved);
                    }

                    if (toExplore.size() % 100000 == 0 || iteration % 100000 == 0) {
                        System.out.println(LocalDateTime.now()
                                + " in queue=" + toExplore.size()
                                + ", processed=" + iteration
                                + ", completed=" + result.size()
                                + "(" + result.stream().map(Burrow::cost).toList() + ")");
                        System.out.println(moved);
                    }
                }
            }
        } while (!toExplore.isEmpty());

        return result;
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(12521, solution1(input.file("test1.txt")));
        //  Took 10 minutes
//        assertEquals(17120, solution1(input));
    }

    long solution2(Input raw) {
        List<String> lines = raw.asListOfStrings();
        lines.add(3, "  #D#C#B#A#");
        lines.add(4, "  #D#B#A#C#");
        Burrow burrow = parseInput(lines, homeRoomsForPart2);

        Set<Burrow> burrows = explore(burrow);

        return burrows.stream()
                .min(comparing(Burrow::cost))
                .map(Burrow::cost)
                .orElseThrow();
    }

    @Test
    void part2() {
        Input input = getInput();
        //  Too slow
//        assertEquals(44169, solution2(input.file("test1.txt")));
        assertEquals(47234, solution2(input));
    }
}
