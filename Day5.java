import static com.horstmann.adventofcode.Util.*;

import com.horstmann.adventofcode.*;

Set<Range> ranges;
Set<Long> ingredients;

void parse(Path path) throws IOException {
    ranges = new TreeSet<>();
    ingredients = new HashSet<>();
    boolean first = true;
    for (String line : Files.readAllLines(path)) {
        if (line.isBlank()) first = false;
        else if (first) ranges.add(Range.parse(line));
        else ingredients.add(Long.parseLong(line));
    }
}

boolean isFresh(long x) {
    return ranges.stream().anyMatch(r -> r.contains(x));
}

Object part1() {
    return ingredients.stream().filter(this::isFresh).count();
}

Set<Range> disjoint(Set<Range> ranges) {
    if (ranges.isEmpty()) return Set.of();
    Range first = ranges.iterator().next();
    Set<Range> disjointRest = disjoint(Sets.difference(ranges, Set.of(first)));
    Set<Range> result = new TreeSet<>();
    for (var r : disjointRest) {
        if (first.touches(r)) 
            first = first.join(r);
        else 
            result.add(r);
    }
    result.add(first);
    return result;
}

Object part2Original() {
    Set<Range> disjointRanges = disjoint(ranges);
    return disjointRanges.stream().mapToLong(Range::size).sum();
}

Object part2() {
    long[] rangeUnion = {};
    for (var r : ranges) 
        rangeUnion = Intervals.union(rangeUnion, new long[] { r.from(), r.to() });
    long result = 0;
    for (int i = 0; i < rangeUnion.length; i += 2)
        result += rangeUnion[i + 1] - rangeUnion[i] + 1;
    return result;
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
