package aoc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.parse;

public class Input {

    private static final String INPUT_TXT = "input.txt";

    private final LocalDate date;
    private final String name;

    public Input(LocalDate date) {
        this(date, INPUT_TXT);
    }

    public Input(LocalDate date, String name) {
        this.date = date;
        this.name = name;
    }

    public static Input forDay(int day) {
        return new Input(parse("2021-12-%02d".formatted(day)));
    }

    public long[] asLongArray() {
        try {
            return Files.readAllLines(fetchInput())
                    .stream()
                    .mapToLong(Long::valueOf)
                    .toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> asListOfStrings() {
        try {
            return Files.readAllLines(fetchInput());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path fetchInput() {
        Path inputPath = buildInputPath();
        if (inputPath.toFile().exists()) return inputPath;

        try {
            if (!inputPath.toFile().getName().equals(INPUT_TXT)) {
                throw new FileNotFoundException(inputPath.toString());
            }

            String session = Files.readString(Path.of("../.session"));
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://adventofcode.com/%d/day/%d/input"
                            .formatted(date.getYear(), date.getDayOfMonth())))
                    .timeout(Duration.ofMinutes(2))
                    .header("Cookie", "session=" + session)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 != response.statusCode()) {
                throw new RuntimeException("Unexpected HTTP response: %d %s"
                        .formatted(response.statusCode(), response.body()));
            }
            //noinspection ResultOfMethodCallIgnored
            inputPath.toFile().getParentFile().mkdir();
            Files.writeString(inputPath, response.body());
            return inputPath;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Path buildInputPath() {
        return Path.of("src/test/resources/%02d".formatted(date.getDayOfMonth()), name);
    }

    public Input file(String name) {
        return new Input(date, name);
    }
}
