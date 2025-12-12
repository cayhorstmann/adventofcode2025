import static com.horstmann.adventofcode.Util.*;

import com.horstmann.adventofcode.*;

record Region(CharGrid grid, List<Integer> presents) {
    static Region parse(String s) {
        var numbers = parseIntegers(s, "[^\\pN]+");
        return new Region(new CharGrid(numbers.get(0), numbers.get(1), ' '), numbers.subList(2, numbers.size()));
    }
    boolean definitelyFits() {
        return 9 * presents.stream().mapToInt(i -> i).sum() <= grid.rows() * grid.cols();
    }
    boolean cantFit() {
        long count = 0;
        for (int i = 0; i < presents.size(); i++) count += sizes.get(i) * presents.get(i);
        return count > grid.rows() * grid.cols();
    }
}

List<CharGrid> shapes;
static List<Long> sizes;
List<Region> regions;

void parse(Path path) throws IOException {
    shapes = new ArrayList<>();
    var inputs = Files.readString(path).split("\n\n");
    for (int i = 0; i < inputs.length - 1; i++) {
        shapes.add(CharGrid.parse(Stream.of(inputs[i].split("\n")).skip(1).toList()));
    }
    sizes = shapes.stream().map(s -> s.findAll('#').count()).toList();
    regions = Stream.of(inputs[inputs.length - 1].split("\n")).map(Region::parse).toList();
}

Object part1() {
    return regions.stream().filter(Region::definitelyFits).count();
}

Object part2() {
    return regions.stream().filter(r -> !r.cantFit()).count();
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
