import com.horstmann.adventofcode.*;
import static com.horstmann.adventofcode.Util.*;

CharGrid grid;

void parse(Path path) throws IOException {
    grid = CharGrid.parse(path);
}

boolean accessible(Location loc) {
    return grid.get(loc) == '@' && 
            grid.allNeighbors(loc)
            .stream()
            .filter(l -> grid.get(l) == '@')
            .count() < 4;
}

Object part1() {
    return grid.locations().filter(this::accessible).count();
}

Object part2() {
    int count = 0;
    boolean done = false;
    while (!done) {
        var ls = grid.locations().filter(this::accessible).toList();
        count += ls.size();
        for (var l : ls) grid.put(l, ' ');
        done = ls.size() == 0;
    }
    return count;
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
