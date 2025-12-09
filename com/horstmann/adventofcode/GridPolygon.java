package com.horstmann.adventofcode;

import module java.base;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static com.horstmann.adventofcode.Direction.*;

/**
 * A GridPolygon is bounded by a path of horizontal and vertical segments.
 */
public class GridPolygon {
    /*
     * A horizontal or vertical segment
     */
    private class Segment {
        int rmin;
        int rmax;
        int cmin;
        int cmax;
        Direction direction;
        
        Segment(int r1, int c1, int r2, int c2) {
            if (r1 == r2 && c1 == c2) 
                throw new IllegalArgumentException("Equal start and end: (%d,%d)-(%d,%d)".formatted(r1, c1, r2, c2));
            if (!(r1 == r2 || c1 == c2)) 
                throw new IllegalArgumentException("Not horizontal or vertical: (%d,%d)-(%d,%d)".formatted(r1, c1, r2, c2));
            rmin = min(r1, r2);
            rmax = max(r1, r2);
            cmin = min(c1, c2);
            cmax = max(c1, c2);
            if (c1 == c2) {
                if (r1 < r2) direction = S;
                else direction = N;
            }
            else if (c1 < c2) direction = E;
            else direction = W;                            
        }
        
        boolean isHorizontal() { return rmin == rmax; }
        boolean isVertical() { return cmin == cmax; }

        boolean contains(int r, int c) {
            return rmin <= r && r <= rmax && cmin <= c && c <= cmax;
        }
        
        boolean contains(Segment other) {
            if (isHorizontal())
                return other.isHorizontal() && cmin <= other.cmin && other.cmax <= cmax;
            else
                return other.isVertical() && rmin <= other.rmin && other.rmax <= rmax;
        }
        
        boolean intersects(Segment other) {
            if (isVertical() && other.isHorizontal()) 
                return rmin <= other.rmin && other.rmin <= rmax && other.cmin <= cmin && cmin <= other.cmax;
            else if (isHorizontal() && other.isVertical())
                return other.rmin <= rmin && rmin <= other.rmax && cmin <= other.cmin && other.cmin <= cmax;
            else if (isVertical() && other.isVertical())
                return cmin == other.cmin && max(rmin, other.rmin) <= min(rmax, other.rmax);
            else
                return rmin == other.rmin && max(cmin, other.cmin) <= min(cmax, other.cmax);
        };
        
        int length() { 
            return max(rmax - rmin, cmax - cmin); // Does not count one of the end points
        }
        
        public String toString() {             
            return direction == E || direction == S 
                    ? "(%d,%d)-(%d,%d)".formatted(rmin, cmin, rmax, cmax)
                    : "(%d,%d)-(%d,%d)".formatted(rmax, cmax, rmin, cmin);
        }
    }
    
    private List<Segment> segments;
    private Set<Location> concaveCorners;
    private int rmax;
    private int cmax;
    private long area;
    private long circumference;
    
    public GridPolygon(List<Location> locations) {
        var a = locations.get(0);        
        area = 0;
        for (int i = 1; i < locations.size(); i++) {
            var b = locations.get(i);
            rmax = max(rmax, b.row());
            cmax = max(cmax, b.col());
            area += a.row() * b.col() - b.row() * a.col(); // Shoelace formula
            a = b;
        }
        var b = locations.get(0);
        area += a.row() * b.col() - b.row() * a.col();
        area /= 2;
        
        boolean clockwise = area < 0;
        if (clockwise) area = -area;
        
        segments = new ArrayList<Segment>();
        concaveCorners = new HashSet<>();
        circumference = 0;
        for (int i = 0; i < locations.size(); i++) {
            a = locations.get(i % locations.size());
            b = locations.get((i + 1) % locations.size());
            var c = locations.get((i + 2) % locations.size());
            var s = new Segment(a.row(), a.col(), b.row(), b.col());
            segments.add(s);
            circumference += s.length();
            var t = new Segment(b.row(), b.col(), c.row(), c.col());
            if (s.direction.turnsTo(t.direction) == (clockwise ? 6 : 2)) 
                concaveCorners.add(b); // 270 degree interior angle
        }            
    }
    
    List<Segment> segmentsInRow(int r) { 
        var result = new ArrayList<Segment>();
        var row = new Segment(r, 0, r, cmax);
        var points = segments.stream()
                .filter(row::intersects)
                .flatMap(seg -> seg.isHorizontal() 
                        ? Stream.of(new Location(seg.rmin, seg.cmin), new Location(seg.rmax, seg.cmax))
                        : Stream.of(new Location(r, seg.cmin))) 
                .sorted(Comparator.comparingInt(Location::col))
                .distinct()
                .toList();
        
        int start = -1;
        for (var p : points) {
            if (start == -1) {
                start = p.col();
            } else if (!concaveCorners.contains(p)) {
                result.add(new Segment(r, start, r, p.col()));  
                start = -1;
            } // with concave corner, keep scanning
        }
        return result;
    }

    List<Segment> segmentsInColumn(int c) { 
        var result = new ArrayList<Segment>();
        var col = new Segment(0, c, rmax, c);
        var points = segments.stream()
                .filter(col::intersects)
                .flatMap(seg -> seg.isVertical() 
                        ? Stream.of(new Location(seg.rmin, seg.cmin), new Location(seg.rmax, seg.cmax))
                        : Stream.of(new Location(seg.rmin, c))) 
                .sorted(Comparator.comparingInt(Location::row))
                .distinct()
                .toList();
        
        int start = -1;
        for (var p : points) {
            if (start == -1) {
                start = p.row();
            } else if (!concaveCorners.contains(p)) {
                result.add(new Segment(start, c, p.row(), c));  
                start = -1;
            } // with concave corner, keep scanning
        }
        return result;
    }
    
    private boolean contains(Segment seg) {
        var segments = seg.isHorizontal() ? segmentsInRow(seg.rmin) : segmentsInColumn(seg.cmin);
        return segments.stream().anyMatch(s -> s.contains(seg));
    }

    public boolean contains(Location a) {
        var segments = segmentsInRow(a.row());
        return segments.stream().anyMatch(s -> s.contains(a.row(), a.col()));
    }
    
    public boolean containsSegment(Location a, Location b) {
        return contains(new Segment(a.row(), a.col(), b.row(), b.col()));
    }
    
    public boolean containsRectangle(Location a, Location b) {
        return contains(new Segment(a.row(), a.col(), a.row(), b.col()))
                && contains(new Segment(a.row(), b.col(), b.row(), b.col()))
                && contains(new Segment(a.row(), a.col(), b.row(), a.col()))
                && contains(new Segment(b.row(), a.col(), b.row(), b.col()));
    }
    
    public long area() { return area; }
    public long circumference() { return circumference; }
    public long interiorPoints() { return area - circumference / 2 + 1; } // Pick's theorem
}