import static com.horstmann.adventofcode.Util.*;

import com.horstmann.adventofcode.*;

record Point3(int x, int y, int z) {
    long distanceSquared(Point3 other) {
        long dx = ((long) x) - other.x;
        long dy = ((long) y) - other.y;
        long dz = ((long) z) - other.z;
        return dx * dx + dy * dy + dz * dz;  
    }
    static Point3 parse(String s) {
        var ss = s.split(",");
        return new Point3(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
    }
}

List<Point3> points;
Map<Point3, Set<Point3>> connections;
int iterations;

void parse(Path path) throws IOException {
    points = Files.lines(path).map(Point3::parse).toList();
    connections = new HashMap<>();
    for (var p : points) connections.put(p, new HashSet<>());
}

List<Point3> closest() {
    long minDist = Long.MAX_VALUE;
    List<Point3> shortestPair = null;
    for (int i = 0; i < points.size(); i++) {
        for (int j = i + 1; j < points.size(); j++) {
            var p = points.get(i);
            var q = points.get(j);
            if (!connections.get(p).contains(q)) {
                long dist = p.distanceSquared(q);
                if (dist < minDist) {
                    shortestPair = List.of(p, q);
                    minDist = dist;
                }
            }
        }
    }
    return shortestPair;
}

List<Point3> connect() {
    var cl = closest();
    connections.get(cl.get(0)).add(cl.get(1));
    connections.get(cl.get(1)).add(cl.get(0));
    return cl;
}

Object part1() {
    // This is slow for part 2, and I could speed it up with union-find, but it's not *that* slow
    for (int i = 0; i < iterations; i++) {
        connect();
    }
    var circuits = Graphs.connectedComponents(points, p -> connections.get(p));
    var circuitsSorted = new TreeSet<Set<Point3>>(Comparator.comparingInt(Set::size));
    circuitsSorted.addAll(circuits);
    var iter = circuitsSorted.descendingIterator();
    var s1 = iter.next().size();
    var s2 = iter.next().size();
    var s3 = iter.next().size();
    return s1 * s2 * s3;
}

Object part2() {
    // Compute weighted edges of complete graph
    var edges = new PriorityQueue<Graphs.WeightedEdge<Point3>>();
    for (int i = 0; i < points.size(); i++) {
        for (int j = i + 1; j < points.size(); j++) {
            var p = points.get(i);
            var q = points.get(j);
            edges.add(new Graphs.WeightedEdge<>(p, q, p.distanceSquared(q)));
        }
    }
    var spanningTree = Graphs.kruskal(points, edges);
    var last = spanningTree.last();
    return last.from().x() * last.to().x();
}

void main() throws Exception {
    parse(inputPath("a"));
    iterations = 10;
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    iterations = 1000;
    time(this::part1);
    time(this::part2);
}