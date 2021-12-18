package aoc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static aoc.Day18.Pair.parsePair;
import static aoc.Input.forDay;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day18 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(18);
        input.fetchInput();
        return input;
    }

    List<Pair> parseInput(Input input) {
        return input.asListOfStrings().stream().map(Pair::parsePair).toList();
    }

    static class Pair {
        Pair parent;

        Pair leftPair;
        Pair rightPair;

        Integer leftNumber;
        Integer rightNumber;

        Pair(Pair parent) {
            this.parent = parent;
        }

        public long magnitude() {
            long leftScore = 3 * (leftNumber != null ? leftNumber : leftPair.magnitude());
            long rightScore = 2 * (rightNumber != null ? rightNumber : rightPair.magnitude());
            return leftScore + rightScore;
        }

        Pair copy(Pair parent) {
            Pair copy = new Pair(parent);
            copy.leftPair = leftPair == null ? null : leftPair.copy(copy);
            copy.rightPair = rightPair == null ? null : rightPair.copy(copy);
            copy.leftNumber = leftNumber;
            copy.rightNumber = rightNumber;

            return copy;
        }

        Pair add(Pair other) {
            Pair result = new Pair(parent);

            result.leftPair = this.copy(result);
            result.rightPair = other.copy(result);

            result.reduce();

            return result;
        }

        Pair reduce() {
            boolean modified;

            do {
                modified = false;

                while (tryExplodeFirst(this, 0)) {
                    modified = true;
                }

                modified |= trySplitFirst(this);
            } while (modified);

            return this;
        }

        private boolean trySplitFirst(Pair pair) {
            if (pair.leftNumber != null && pair.leftNumber > 9) {
                Pair split = new Pair(pair);
                split.leftNumber = (int) Math.floor(pair.leftNumber / 2.0);
                split.rightNumber = (int) Math.ceil(pair.leftNumber / 2.0);
                pair.leftNumber = null;
                pair.leftPair = split;
                return true;
            }

            if (pair.leftPair != null) {
                if (trySplitFirst(pair.leftPair)) {
                    return true;
                }
            }

            if (pair.rightNumber != null && pair.rightNumber > 9) {
                Pair split = new Pair(pair);
                split.leftNumber = (int) Math.floor(pair.rightNumber / 2.0);
                split.rightNumber = (int) Math.ceil(pair.rightNumber / 2.0);
                pair.rightNumber = null;
                pair.rightPair = split;
                return true;
            }

            if (pair.rightPair != null) {
                return trySplitFirst(pair.rightPair);
            }

            return false;
        }

        private static boolean tryExplodeFirst(Pair pair, int level) {
            if (level == 4) {
                pair.explode();
                return true;
            }

            if (pair.leftPair != null) {
                if (tryExplodeFirst(pair.leftPair, level + 1)) {
                    return true;
                }
            }

            if (pair.rightPair != null) {
                return tryExplodeFirst(pair.rightPair, level + 1);
            }

            return false;
        }

        private void explode() {
            assert leftNumber != null && rightNumber != null;

            Pair root = findRoot(parent);

            Map<Integer, Index> index = new HashMap<>();
            indexPairs(root, index);

            int indexOfThisLeft = index
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().pair.equals(this) && entry.getValue().left)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();

            int indexOfThisRight = index
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().pair.equals(this) && !entry.getValue().left)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();

            //  rightNumber -> first node on the right-hand side with number on the left
            for (int i = indexOfThisLeft - 1; i >= 0; i--) {
                Pair pair = index.get(i).pair;
                if (pair == this) continue;
                if (pair.rightNumber != null) {
                    pair.rightNumber += leftNumber;
                    break;
                }
                if (pair.leftNumber != null) {
                    pair.leftNumber += leftNumber;
                    break;
                }
            }

            //  leftNumber -> first node on the left-hand side with number on the right
            for (int i = indexOfThisRight + 1; i < index.size(); i++) {
                Pair pair = index.get(i).pair;
                if (pair == this) continue;
                if (pair.leftNumber != null) {
                    pair.leftNumber += rightNumber;
                    break;
                }
                if (pair.rightNumber != null) {
                    pair.rightNumber += rightNumber;
                    break;
                }
            }

            //  Detaching self from parent and replacing with 0
            if (parent.leftPair == this) {
                parent.leftPair = null;
                parent.leftNumber = 0;
            } else if (parent.rightPair == this) {
                parent.rightPair = null;
                parent.rightNumber = 0;
            }

            parent = null;
        }

        record Index(boolean left, Pair pair) {
            @Override
            public String toString() {
                return left ? "left of " + pair : "right of " + pair;
            }
        }

        private void indexPairs(Pair pair, Map<Integer, Index> labels) {
            if (pair.leftNumber != null) {
                labels.put(labels.size(), new Index(true, pair));
            }
            if (pair.leftPair != null) {
                indexPairs(pair.leftPair, labels);
            }
            if (pair.rightNumber != null) {
                labels.put(labels.size(), new Index(false, pair));
            }
            if (pair.rightPair != null) {
                indexPairs(pair.rightPair, labels);
            }
        }

        private Pair findRoot(Pair pair) {
            Pair root = pair.parent;
            if (root == null) return pair;
            while (root.parent != null) {
                root = root.parent;
            }
            return root;
        }

        @Override
        public String toString() {
            return "[%s,%s]".formatted(
                    leftPair != null ? leftPair : leftNumber,
                    rightPair != null ? rightPair : rightNumber);
        }

        static Pair parsePair(String value) {
            Stack<Pair> stack = new Stack<>();
            Pair result = null;
            for (int i = 0; i < value.length(); i++) {
                char ch = value.charAt(i);
                if (ch == ',') continue;
                if (ch == '[') {
                    Pair parent = stack.isEmpty() ? null : stack.peek();
                    Pair self = new Pair(parent);
                    stack.push(self);
                    if (parent != null) {
                        if (parent.leftEmpty()) {
                            parent.leftPair = self;
                        } else {
                            parent.rightPair = self;
                        }
                    }
                } else if (ch == ']') {
                    result = stack.pop();
                } else {
                    int number = Integer.parseInt(Character.toString(ch));
                    Pair self = stack.peek();
                    if (self.leftEmpty()) {
                        self.leftNumber = number;
                    } else {
                        self.rightNumber = number;
                    }
                }
            }
            return result;
        }

        private boolean leftEmpty() {
            return leftPair == null && leftNumber == null;
        }
    }

    Pair solution1(Input raw) {
        List<Pair> input = parseInput(raw);

        return input.stream().reduce(Pair::add).orElseThrow();
    }

    @Test
    public void testAdd() {
        assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]",
                parsePair("[[[[4,3],4],4],[7,[[8,4],9]]]")
                        .add(parsePair("[1,1]"))
                        .toString());

        assertEquals("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
                parsePair("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]")
                        .add(parsePair("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"))
                        .toString());

        assertEquals("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]",
                parsePair("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")
                        .add(parsePair("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"))
                        .toString());

        assertEquals("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]",
                parsePair("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]")
                        .add(parsePair("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"))
                        .toString());

        assertEquals("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]",
                parsePair("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")
                        .add(parsePair("[7,[5,[[3,8],[1,4]]]]"))
                        .toString());
    }

    @Test
    public void testSum() {
        assertEquals("[[[[1,1],[2,2]],[3,3]],[4,4]]",
                parsePair("[1,1]")
                        .add(parsePair("[2,2]"))
                        .add(parsePair("[3,3]"))
                        .add(parsePair("[4,4]"))
                        .toString());

        assertEquals("[[[[3,0],[5,3]],[4,4]],[5,5]]",
                parsePair("[1,1]")
                        .add(parsePair("[2,2]"))
                        .add(parsePair("[3,3]"))
                        .add(parsePair("[4,4]"))
                        .add(parsePair("[5,5]"))
                        .toString());

        assertEquals("[[[[5,0],[7,4]],[5,5]],[6,6]]",
                parsePair("[1,1]")
                        .add(parsePair("[2,2]"))
                        .add(parsePair("[3,3]"))
                        .add(parsePair("[4,4]"))
                        .add(parsePair("[5,5]"))
                        .add(parsePair("[6,6]"))
                        .toString());

        List<Pair> pairs = parseInput(forDay(18).file("test2.txt"));
        Pair pair = pairs.stream().reduce(Pair::add).orElseThrow();
        assertEquals("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", pair.toString());
    }

    @Test
    public void testReduce() {
        assertEquals("[[[[0,9],2],3],4]", parsePair("[[[[[9,8],1],2],3],4]").reduce().toString());
        assertEquals("[7,[6,[5,[7,0]]]]", parsePair("[7,[6,[5,[4,[3,2]]]]]").reduce().toString());
        assertEquals("[[6,[5,[7,0]]],3]", parsePair("[[6,[5,[4,[3,2]]]],1]").reduce().toString());
        assertEquals("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", parsePair("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").reduce().toString());
    }

    @Test
    public void testMagnitude() {
        assertEquals(143, parsePair("[[1,2],[[3,4],5]]").magnitude());
        assertEquals(1384, parsePair("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude());
        assertEquals(445, parsePair("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude());
        assertEquals(791, parsePair("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude());
        assertEquals(3488, parsePair("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude());
    }

    @Test
    void part1() {
        Input input = getInput();
        Pair test1 = solution1(input.file("test1.txt"));
        assertEquals("[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]", test1.toString());
        assertEquals(4140, test1.magnitude());
        assertEquals(4137, solution1(input).magnitude());
    }

    long solution2(Input raw) {
        List<Pair> input = parseInput(raw);

        long max = 0;

        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.size(); j++) {
                if (i == j) continue;
                long magnitude = input.get(i).add(input.get(j)).magnitude();
                if (magnitude > max) {
                    max = magnitude;
                }
            }
        }

        return max;
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(3993, solution2(input.file("test1.txt")));
        assertEquals(4573, solution2(input));
    }
}
