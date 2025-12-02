import static com.horstmann.adventofcode.Util.*;

List<Range> ranges;

record Range(long from, long to) {
    static Range parse(String s) {
        var ss = s.split("-");
        return new Range(Long.parseLong(ss[0].strip()), Long.parseLong(ss[1].strip()));
    }
    
    long matchSum(LongPredicate p) {
        return LongStream.rangeClosed(from, to).filter(p).sum();
    }
    
    static boolean invalid(long idnum) {
        String id = "" + idnum;
        int n = id.length();
        return n % 2 == 0 && id.substring(0, n / 2).equals(id.substring(n / 2));
    }
    
    static boolean invalid2(long idnum) {
        String id = "" + idnum;
        int n = id.length();
        for (int k = 1; k <= n / 2; k++) {
            if (n % k == 0) {
                boolean equal = true;
                var first = id.substring(0, k);
                for (int i = 1; equal && i < n / k; i++) 
                    equal = first.equals(id.substring(i * k, (i + 1) * k));
                if (equal) return true;
            }
        }
        return false;
    }
}

void parse(Path path) throws IOException {
    ranges = Stream.of(Files.readString(path).split(",")).map(Range::parse).toList();
}

Object part1() {
    return ranges.stream().mapToLong(r -> r.matchSum(Range::invalid)).sum();
}

Object part2() {
    return ranges.stream().mapToLong(r -> r.matchSum(Range::invalid2)).sum();
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
