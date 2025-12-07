import com.horstmann.adventofcode.*;
import static com.horstmann.adventofcode.Util.*;

CharGrid grid;
Location start;
Set<Location> splitters;

void parse(Path path) throws IOException {
    grid = CharGrid.parse(path);
    start = grid.findFirst('S');
    splitters = grid.findAll('^').collect(Collectors.toSet());
}

Object part1() {
    var beams = new TreeSet<Integer>();
    beams.add(start.col());
    int splits = 0;
    
    for (int row = 2; row < grid.rows(); row += 2) {
        var newBeams = new TreeSet<Integer>();
        for (var beam : beams) {
            if (splitters.contains(new Location(row, beam))) {
                newBeams.add(beam - 1);
                newBeams.add(beam + 1);
                splits++;
            } else {
                newBeams.add(beam);
            }
        }
        beams = newBeams;
    }
    return splits;
}

Map<Location, Long> memo;

long paths(Location start) {
    if (memo.containsKey(start)) return memo.get(start);
    long result;
    if (start.row() == grid.rows() - 1) {
        result = 1;
    } else {
        var below = start.moved(Direction.S);
        if (splitters.contains(below)) {
            result = paths(below.moved(Direction.SW)) + paths(below.moved(Direction.SE));  
        } else {
            result = paths(below.moved(Direction.S));
        }
    }
    memo.put(start, result);
    return result;
}

Object part2() {
    memo = new HashMap<>();
    return paths(start.moved(Direction.S));
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}