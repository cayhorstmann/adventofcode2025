import static com.horstmann.adventofcode.Util.*;

import com.horstmann.adventofcode.*;

Map<String, Set<String>> neighbors;

void parse(Path path) throws IOException {
    neighbors = new HashMap<>();
    for (String line : Files.readAllLines(path)) {
        String[] tokens = line.split(": ");
        neighbors.put(tokens[0], Set.of(tokens[1].split(" ")));
    }
}

Object part1() {
    var paths = Graphs.simplePaths("you", "out", neighbors::get);
    return paths.size();
}

Object part2() {
    // By inspecting the dot output, the graph is a DAG
    // IO.println(Graphs.dot("svr", n -> neighbors.computeIfAbsent(n, _ -> Set.of()), (_, _) -> null));
    Function<String, Set<String>> neighborFunction = n -> neighbors.computeIfAbsent(n, _ -> Set.of());
    long c1 = Graphs.dagPathCount("svr", "fft", neighborFunction);
    long c2 = Graphs.dagPathCount("fft", "dac", neighborFunction);
    long c3 = Graphs.dagPathCount("dac", "out", neighborFunction);
    long c4 = Graphs.dagPathCount("svr", "dac", neighborFunction);
    long c5 = Graphs.dagPathCount("dac", "fft", neighborFunction);
    long c6 = Graphs.dagPathCount("fft", "out", neighborFunction);
    return c1 * c2 * c3 + c4 * c5 * c6; // One of the summands will be zero
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    parse(inputPath("b"));
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
