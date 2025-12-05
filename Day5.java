import static com.horstmann.adventofcode.Util.*;
import static java.lang.Math.*;

import com.horstmann.adventofcode.Sets;

record Range(long from, long to) {
    static Range parse(String s) {
        var ss = s.split("-");
        return new Range(Long.parseLong(ss[0]), Long.parseLong(ss[1]));
    }
    boolean contains(long x) {
        return from <= x && x <= to;
    }
    boolean overlaps(Range other) {
        return max(from, other.from) <= min(to,  other.to); 
    }
    Range join(Range other) {
        return new Range(min(from, other.from), max(to, other.to));
    }
    long length() {
        return max(0, to - from + 1);
    }
    boolean isEmpty() {
        return from > to;
    }
}

Set<Range> ranges;
Set<Long> ingredients;

void parse(Path path) throws IOException {
    ranges = new HashSet<>();
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

Set<Range> disjoint(Set<Range> ranges) {
    if (ranges.isEmpty()) return Set.of();
    Range first = ranges.iterator().next();
    Set<Range> disjointRest = disjoint(Sets.difference(ranges, Set.of(first)));
    Set<Range> result = new HashSet<>();
    for (var r : disjointRest) {
        if (first.overlaps(r)) 
            first = first.join(r);
        else 
            result.add(r);
    }
    result.add(first);
    return result;
}

Object part1() {
    return ingredients.stream().filter(this::isFresh).count();
}

Object part2() {
    Set<Range> disjointRanges = disjoint(ranges);
    return disjointRanges.stream().mapToLong(Range::length).sum();
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
