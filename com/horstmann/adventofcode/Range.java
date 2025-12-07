package com.horstmann.adventofcode;
import module java.base;
import static java.lang.Math.*;

public record Range(long from, long to) implements Comparable<Range> {
    public static Range of(long from, long to) { 
        return new Range(from, to); 
    }
    public static Range parse(String s) {
        var ss = s.split("-");
        return new Range(Long.parseLong(ss[0]), Long.parseLong(ss[1]));
    }
    public boolean contains(long x) {
        return from <= x && x <= to;
    }
    public boolean touches(Range other) {
        return max(from, other.from) <= min(to,  other.to); 
    }
    public boolean contains(Range other) {
        return from <= other.from && other.to <= to;
    }
    public Range intersect(Range other) {
        return new Range(max(from, other.from), min(to, other.to));
    }
    public Range join(Range other) {
        return new Range(min(from, other.from), max(to, other.to));
    }
    public long size() {
        return max(0, to - from + 1);
    }
    public boolean isEmpty() {
        return from > to;
    }
    public int compareTo(Range other) {
        int d = Long.compare(from, other.from);
        return d != 0 ? d : Long.compare(to, other.to);
    }
}
