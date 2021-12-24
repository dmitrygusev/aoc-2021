package aoc;

import org.junit.jupiter.api.Test;

import java.util.*;

import static aoc.Input.forDay;
import static java.util.Arrays.stream;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.*;

class Day24 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(24);
        input.fetchInput();
        return input;
    }

    List<String> parseInput(Input input) {
        return input.asListOfStrings();
    }

    long printProgram(Input raw) {
        List<String> input = parseInput(raw);

        StringBuilder builder = new StringBuilder();
        builder.append("""
                static boolean monad(int[] input) {
                        int w = 0, x = 0, y = 0, z = 0;
                        int i = 0;
                """);
        input.forEach(line -> {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "inp":
                    builder.append("        w = input[i++];\n");
                    break;
                case "add":
                    builder.append("        %s += %s;\n".formatted(parts[1], parts[2]));
                    break;
                case "mul":
                    builder.append("        %s *= %s;\n".formatted(parts[1], parts[2]));
                    break;
                case "div":
                    builder.append("        %s = %s / %s;\n".formatted(parts[1], parts[1], parts[2]));
                    break;
                case "mod":
                    builder.append("        %s = %s %% %s;\n".formatted(parts[1], parts[1], parts[2]));
                    break;
                case "eql":
                    builder.append("        %s = %s == %s ? 1 : 0;\n".formatted(parts[1], parts[1], parts[2]));
                    break;
            }
        });
        builder.append("        return z == 0;\n    }");

        System.out.println(builder);

        return 0;
    }

    static boolean monad(int[] input) {
        int x = 0, y = 0, z = 0;

        int a, b, c;

        z = push(input[0], z, 11, 5);    //  13 <--------.
        z = push(input[1], z, 13, 5);    //  12 <------.  \
        z = push(input[2], z, 12, 1);    //   9 <----.  \  \
        z = push(input[3], z, 15, 15);   //   8 <---. \  \  \
        z = push(input[4], z, 10, 2);    //   5 <--, \ \  \  \
        z = pop(input[5], z, -1, 2);     //   4  _/  |  |  |  |
        z = push(input[6], z, 14, 5);    //   7 <-, /  /  /  /
        z = pop(input[7], z, -8, 8);     //   6 _/ /  /  /  /
        z = pop(input[8], z, -7, 14);    //   3 __/  /  /  /
        z = pop(input[9], z, -8, 12);    //   2 ____/  /  /
        z = push(input[10], z, 11, 7);   //  11 <-,   /  /
        z = pop(input[11], z, -2, 14);   //  10 _/   /  /
        z = pop(input[12], z, -2, 13);   //   1 ____/  /
        z = pop(input[13], z, -13, 6);   //   0 ______/

        /*
        Each pop should compensate corresponding push as in stack:

            i[pushed] + c = i[popped] - b

        i[0]+5=i[13]+13
        i[1]+5=i[12]+2
        i[2]+1=i[9]+8
        i[3]+15=i[8]+7
        i[4]+2=i[5]+1
        i[6]+5=i[7]+8
        i[10]+7=i[11]+2
         */

        return z == 0;
    }

    private static int push(int w, int z, int b, int c) {
        int x = z % 26 + b != w ? 1 : 0;
        z *= 25 * x + 1;
        z += (w + c) * x;
//        System.out.println("pushed z=" + z + ", /26=" + (z / 26) + ", %/26=" + (z%26) + ", w=" + w + ", b=" + b + ", c=" + c);
        return z;
    }

    private static int pop(int w, int z, int b, int c) {
        int x = z % 26 + b != w ? 1 : 0;
        z /= 26;
        z *= 25 * x + 1;
        z += (w + c) * x;
//        System.out.println("popped z=" + z + ", /26=" + (z / 26) + ", %/26=" + (z%26) + ", w=" + w + ", b=" + b + ", c=" + c);
        return z;
    }

    @Test
    void solve() {
        Set<String> result = new HashSet<>();

        for (int i1 = 9; i1 >= 1; i1--)
            for (int i2 = 9; i2 >= 1; i2--)
                for (int i3 = 9; i3 >= 1; i3--)
                    for (int i4 = 9; i4 >= 1; i4--)
                        for (int i6 = 9; i6 >= 1; i6--)
                            for (int i10 = 9; i10 >= 1; i10--)
                                for (int i13 = 9; i13 >= 1; i13--) {

                                    int i0 = i13 + 13 - 5;
                                    int i5 = i4 + 2 - 1;
                                    int i7 = i6 + 5 - 8;
                                    int i8 = i3 + 15 - 7;
                                    int i9 = i2 + 1 - 8;
                                    int i11 = i10 + 7 - 2;
                                    int i12 = i1 + 5 - 2;

                                    if (i5 < 1 || i5 > 9
                                            || i7 < 1 || i7 > 9
                                            || i8 < 1 || i8 > 9
                                            || i9 < 1 || i9 > 9
                                            || i11 < 1 || i11 > 9
                                            || i12 < 1 || i12 > 9
                                            || i0 < 1 || i0 > 9) continue;

                                    int[] input = {i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13};
                                    if (monad(input)) {
                                        result.add(stream(input)
                                                .mapToObj(String::valueOf)
                                                .collect(joining()));
                                    }
                                }

        assertNotNull(result);
        assertNotEquals(0, result.size());
        //  Part 1
        assertEquals("96918996924991", result.stream().max(naturalOrder()).orElseThrow());

        //  Part 2
        String min = result.stream().min(naturalOrder()).orElseThrow();
        assertNotEquals("101811241911642", min, "Too high");
        assertEquals("91811241911641", min);
    }

    @Test
    void debug() {
        Input input = getInput();
        printProgram(input);
    }
}
