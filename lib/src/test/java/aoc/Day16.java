package aoc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static aoc.Input.forDay;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.toBinaryString;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day16 {
    @Test
    void fetchInput() {
        getInput();
    }

    private Input getInput() {
        Input input = forDay(16);
        input.fetchInput();
        return input;
    }

    String parseInput(Input input) {
        return input.asListOfStrings().get(0);
    }

    static class BITS {
        int index;
        final String data;

        public BITS(String bin) {
            this.data = bin;
        }

        public static BITS fromHex(String hex) {
            return new BITS(hexToBin(hex));
        }

        static String hexToBin(String hex) {
            return hex.chars()
                    .mapToObj(Character::toString)
                    .map(s -> parseInt(s, 16))
                    .map(i -> format("%04d", parseInt(toBinaryString(i))))
                    .collect(joining());
        }

        public Packet read() {
            return Packet.read(this);
        }

        private int readBits(int n) {
            try {
                return parseInt(data.substring(index, index + n), 2);
            } finally {
                index += n;
            }
        }
    }

    static final class Operator extends Packet {

        private final Function<Operator, Long> function;

        public Operator(int version, int typeId, List<Packet> subPackets, Function<Operator, Long> function) {
            super(version, typeId, subPackets);
            this.function = function;
        }

        public static Packet read(BITS bits, int version, int typeId) {
            int lengthTypeId = bits.readBits(1);
            List<Packet> subPackets = new ArrayList<>();
            switch (lengthTypeId) {
                case 0 -> {
                    int totalLength = bits.readBits(15);
                    int start = bits.index;
                    while (bits.index - start < totalLength) {
                        subPackets.add(bits.read());
                    }
                }
                case 1 -> {
                    int totalSubPackets = bits.readBits(11);
                    for (int i = 0; i < totalSubPackets; i++) {
                        subPackets.add(bits.read());
                    }
                }
            }

            Function<Operator, Long> function = switch (typeId) {
                case 0 -> operator -> operator.subPackets.stream().mapToLong(Packet::eval).sum();
                case 1 -> operator -> operator.subPackets.stream().mapToLong(Packet::eval).reduce(1, (a, b) -> a * b);
                case 2 -> operator -> operator.subPackets.stream().mapToLong(Packet::eval).min().orElseThrow();
                case 3 -> operator -> operator.subPackets.stream().mapToLong(Packet::eval).max().orElseThrow();
                case 5 -> operator -> operator.subPackets.get(0).eval() > operator.subPackets.get(1).eval() ? 1L : 0L;
                case 6 -> operator -> operator.subPackets.get(0).eval() < operator.subPackets.get(1).eval() ? 1L : 0L;
                case 7 -> operator -> operator.subPackets.get(0).eval() == operator.subPackets.get(1).eval() ? 1L : 0L;
                default -> throw new IllegalStateException("Unexpected value: " + typeId);
            };

            return new Operator(version, typeId, subPackets, function);
        }

        @Override
        public long eval() {
            return function.apply(this);
        }
    }

    static final class Literal extends Packet {

        final long value;

        private Literal(long value, int version, int typeId) {
            super(version, typeId, List.of());
            this.value = value;
        }

        public static Literal read(BITS bits, int version, int typeId) {
            StringBuilder builder = new StringBuilder();
            while (true) {
                builder.append(bits.data, bits.index + 1, bits.index + 1 + 4);
                try {
                    if (bits.data.charAt(bits.index) == '0') break;
                } finally {
                    bits.index += 5;
                }
            }
            return new Literal(parseLong(builder.toString(), 2), version, typeId);
        }

        @Override
        public long eval() {
            return value;
        }
    }

    static sealed abstract class Packet {
        final int version;
        final int typeId;
        final List<Packet> subPackets;

        public Packet(int version, int typeId, List<Packet> subPackets) {
            this.version = version;
            this.typeId = typeId;
            this.subPackets = subPackets;
        }

        public static Packet read(BITS bits) {
            int version = bits.readBits(3);
            int typeId = bits.readBits(3);

            if (typeId == 4) {
                return Literal.read(bits, version, typeId);
            }

            return Operator.read(bits, version, typeId);
        }

        public abstract long eval();
    }

    int visit(Packet packet) {
        return packet.version + packet.subPackets.stream().mapToInt(this::visit).sum();
    }

    int solution1(String input) {
        BITS bits = BITS.fromHex(input);

        Packet packet = bits.read();

        return visit(packet);
    }

    @Test
    public void testHexToBin() {
        assertEquals("110100101111111000101000", BITS.hexToBin("D2FE28"));
    }

    @Test
    public void testPacket() {
        BITS bits = BITS.fromHex("D2FE28");
        Packet packet = bits.read();
        assertEquals(Literal.class, packet.getClass());
        Literal literal = (Literal) packet;
        assertEquals(2021, literal.value);
    }

    @Test
    public void testPacket2() {
        BITS bits = BITS.fromHex("38006F45291200");
        Packet packet = bits.read();
        assertEquals(Operator.class, packet.getClass());
        assertEquals(2, packet.subPackets.size());
        assertEquals(10, ((Literal) packet.subPackets.get(0)).value);
        assertEquals(20, ((Literal) packet.subPackets.get(1)).value);
    }

    @Test
    void part1() {
        Input input = getInput();
        assertEquals(16, solution1("8A004A801A8002F478"));
        assertEquals(12, solution1("620080001611562C8802118E34"));
        assertEquals(23, solution1("C0015000016115A2E0802F182340"));
        assertEquals(31, solution1("A0016C880162017C3686B18A3D4780"));
        assertEquals(989, solution1(parseInput(input)));
    }

    long solution2(String input) {
        BITS bits = BITS.fromHex(input);

        Packet packet = bits.read();

        return packet.eval();
    }

    @Test
    void part2() {
        Input input = getInput();
        assertEquals(3, solution2("C200B40A82"));
        assertEquals(54, solution2("04005AC33890"));
        assertEquals(7, solution2("880086C3E88112"));
        assertEquals(9, solution2("CE00C43D881120"));
        assertEquals(1, solution2("D8005AC2A8F0"));
        assertEquals(0, solution2("F600BC2D8F"));
        assertEquals(0, solution2("9C005AC2F8F0"));
        assertEquals(1, solution2("9C0141080250320F1802104A08"));
        assertEquals(7936430475134L, solution2(parseInput(input)));
    }
}
