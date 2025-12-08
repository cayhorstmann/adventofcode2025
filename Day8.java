import com.horstmann.adventofcode.*;
import static com.horstmann.adventofcode.Util.*;

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
int iterations;

void parse(Path path) throws IOException {
    points = Files.lines(path).map(Point3::parse).toList();
}

Object part1() {
    Map<Point3, Set<Point3>> connections = new HashMap<>();
    for (var p : points) connections.put(p, new HashSet<>());
    var pq = new PriorityQueue<Graphs.WeightedEdge<Point3>>();
    for (int i = 0; i < points.size(); i++) {
        for (int j = i + 1; j < points.size(); j++) {
            var p = points.get(i);
            var q = points.get(j);
            pq.add(new Graphs.WeightedEdge<Point3>(p, q, p.distanceSquared(q)));
        }
    }
    for (int i = 0; i < iterations; i++) {
        var e = pq.remove();
        connections.get(e.from()).add(e.to());
        connections.get(e.to()).add(e.from());
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