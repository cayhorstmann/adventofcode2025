import com.horstmann.adventofcode.*;
import static com.horstmann.adventofcode.Util.*;
import static java.lang.Math.*;

List<Integer> turns;

void parse(Path path) throws IOException {
    turns = Files.lines(path).map(line -> Integer.parseInt(line.substring(1))
            * (line.startsWith("L") ? -1 : 1)).toList();
}

int part1() {
    int zeroes = 0;
    int sum = 50;
    for (var t : turns) {
        sum = floorMod(sum + t, 100);
        if (sum == 0) zeroes++;
    }
    return zeroes;
}

int zeroes(int start, int end) {
    if (start <= end) {
        return floorDiv(end, 100) - floorDiv(start, 100);
    }
    else {
        return zeroes(end - 1, start - 1);
    }
}

int part2() {
    int zeroes = 0;
    int sum = 50;
    for (var t : turns) {
        zeroes += zeroes(sum, sum + t);
        sum += t;
    }
    return zeroes;
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
}
