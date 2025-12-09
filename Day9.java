import static com.horstmann.adventofcode.Util.*;

import com.horstmann.adventofcode.*;

List<Location> locations;

void parse(Path path) throws IOException {
    locations = Files.lines(path).map(line -> {
        var tokens = line.split(",");
        return new Location(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[0]));
    }).toList();
    
}

Object part1() {
    long largest = 0;    
    for (int i = 0; i < locations.size(); i++) {
        for (int j = i + 1; j < locations.size(); j++) {
            var a = locations.get(i);
            var b = locations.get(j);
            long area = (Math.abs(a.row() - b.row()) + 1L) * (Math.abs(a.col() - b.col()) + 1L);
            if (area > largest) largest = area;
        }
    }
    return largest;
}

Object part2() {
    var poly = new GridPolygon(locations);
    long largest = 0;    
    for (int i = 0; i < locations.size(); i++) {
        for (int j = i + 1; j < locations.size(); j++) {
            var a = locations.get(i);
            var b = locations.get(j);
            if (a.row() != b.row() && a.col() != b.col() && poly.containsRectangle(a, b)) {
                long area = (Math.abs(a.row() - b.row()) + 1L) * (Math.abs(a.col() - b.col()) + 1L);
                if (area > largest) largest = area;
            }
        }
    }            
    return largest;
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
