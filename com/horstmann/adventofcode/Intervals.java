package com.horstmann.adventofcode;

import module java.base;
import java.io.IOException;

/**
 * A union of intervals is given as a strictly increasing sequence of pairs
 * a[0] a[1] ... a[2*n-2] a[2*n-1]
 * If interpreted as half-open intervals, this is the union
 * [a[0] a[1]) ∪ [a[2] a[3]) ∪ ... ∪ [a[2*n-2] a[2*n-1])
 * If interpreted as closed intervals, it is instead
 * [a[0] a[1]] ∪ [a[2] a[3]] ∪ ... ∪ [a[2*n-2] a[2*n-1]]
 * The following methods compute the representations of the union, intersection, and (for the half-open
 * case), the difference, as a strictly increasing sequence of pairs
 * Note: For union only, the same computation is correct for open and closed intervals. 
 * For intersection, there is a subtle difference when intervals touch. Call with closed = true/false 
 * Don't use difference with closed interval unions--the result is not a union of closed intervals.
 */
public class Intervals {
    public static long[] union(long[] a, long[] b) {
        long[] result = new long[a.length + b.length];
        if (a.length == 0) return b;
        if (b.length == 0) return a;
        int n = 0;
        int i = 0;
        int j = 0;
        while (i < a.length || j < b.length) {
            if (i < a.length && (j == b.length || a[i] <= b[j])) {
                if (j % 2 == 0) { // b closed
                    result[n] = a[i]; n++;
                }
                i++;
            } else if (j < b.length) { // i == a.length || a[i] > b[j]
                if (j % 2 == 0) {
                    // b opening
                    if (i % 2 == 0) { // a closed 
                        if (n > 0 && result[n - 1] == b[j]) n--; // extend prior 
                        else { result[n] = b[j]; n++; } 
                    }
                } else {
                    // b closing 
                    if (i % 2 == 0) { result[n] = b[j]; n++; }
                }
                j++;
            }            
        }
        
        return n == result.length ? result : Arrays.copyOf(result, n);        
    }
    
    public static long[] intersection(long[] a, long[] b, boolean closed) {
        long[] result = new long[a.length + b.length];
        if (a.length == 0) return b;
        if (b.length == 0) return a;
        int n = 0;
        int i = 0;
        int j = 0;
        while (i < a.length || j < b.length) {
            if (i < a.length && j < b.length && a[i] == b[j] && i % 2 != j % 2) {
                if (closed) {
                    result[n] = a[i]; n++;
                    result[n] = a[i]; n++;
                }
                i++;
                j++;
            }
            else if (i < a.length && (j == b.length || a[i] <= b[j])) {
                if (j % 2 != 0) { // b opened 
                    result[n] = a[i]; n++; 
                }
                i++;
            } else if (j < b.length) { // i == a.length || a[i] > b[j]
                if (i % 2 != 0) { // a opened
                    result[n] = b[j]; n++; 
                }
                j++;
            }            
        }
        
        return n == result.length ? result : Arrays.copyOf(result, n);        
    }    
    
    public static long[] difference(long[] a, long[] b) {
        long[] result = new long[a.length + b.length];
        if (a.length == 0) return b;
        if (b.length == 0) return a;
        int n = 0;
        int i = 0;
        int j = 0;
        while (i < a.length || j < b.length) {
            if (i < a.length && (j == b.length || a[i] < b[j])) {
                if (j % 2 == 0) { // b closed 
                    result[n] = a[i]; n++; 
                }
                i++;
            } else if (j < b.length) { // i == a.length || a[i] >= b[j]
                if (i % 2 != 0) { // a opened
                    result[n] = b[j]; n++; 
                }
                j++;
            }            
        }
        
        return n == result.length ? result : Arrays.copyOf(result, n);        
    }    

    // java -ea com/horstmann/adventofcode/Intervals.java 
    void main() throws IOException {
        assert (Arrays.equals(new long[] { 1, 11 }, union(new long[] { 1, 10 }, new long[] { 2, 11 })));
        assert (Arrays.equals(new long[] { 1, 11 }, union(new long[] { 2, 11 }, new long[] { 1, 10 })));
        assert (Arrays.equals(new long[] { 1, 10 }, union(new long[] { 1, 10 }, new long[] { 2, 9 })));
        assert (Arrays.equals(new long[] { 1, 10 }, union(new long[] { 2, 9 }, new long[] { 1, 10 })));
        assert (Arrays.equals(new long[] { 0, 10 }, union(new long[] { 1, 10 }, new long[] { 0, 9 })));
        assert (Arrays.equals(new long[] { 0, 10 }, union(new long[] { 0, 9 }, new long[] { 1, 10 })));
        assert (Arrays.equals(new long[] { 1, 9 }, union(new long[] { 1, 5 }, new long[] { 5, 9 })));
        assert (Arrays.equals(new long[] { 1, 9 }, union(new long[] { 5, 9 }, new long[] { 1, 5 })));
        assert (Arrays.equals(new long[] { 1, 4, 5, 9 }, union(new long[] { 1, 4 }, new long[] { 5, 9 })));
        assert (Arrays.equals(new long[] { 1, 4, 5, 9 }, union(new long[] { 5, 9 }, new long[] { 1, 4 })));

        assert (Arrays.equals(new long[] { 2, 10 }, intersection(new long[] { 1, 10 }, new long[] { 2, 11 }, false)));
        assert (Arrays.equals(new long[] { 2, 10 }, intersection(new long[] { 2, 11 }, new long[] { 1, 10 }, false)));
        assert (Arrays.equals(new long[] { 2, 9 }, intersection(new long[] { 1, 10 }, new long[] { 2, 9 }, false)));
        assert (Arrays.equals(new long[] { 2, 9 }, intersection(new long[] { 2, 9 }, new long[] { 1, 10 }, false)));
        assert (Arrays.equals(new long[] { 1, 9 }, intersection(new long[] { 1, 10 }, new long[] { 0, 9 }, false)));
        assert (Arrays.equals(new long[] { 1, 9 }, intersection(new long[] { 0, 9 }, new long[] { 1, 10 }, false)));
        assert (Arrays.equals(new long[] { }, intersection(new long[] { 1, 5 }, new long[] { 5, 9 }, false)));
        assert (Arrays.equals(new long[] { }, intersection(new long[] { 5, 9 }, new long[] { 1, 5 }, false)));
        assert (Arrays.equals(new long[] { 5, 5 }, intersection(new long[] { 1, 5 }, new long[] { 5, 9 }, true)));
        assert (Arrays.equals(new long[] { 5, 5 }, intersection(new long[] { 5, 9 }, new long[] { 1, 5 }, true)));
        assert (Arrays.equals(new long[] { }, intersection(new long[] { 1, 4 }, new long[] { 5, 9 }, false)));
        assert (Arrays.equals(new long[] { }, intersection(new long[] { 5, 9 }, new long[] { 1, 4 }, false)));

        assert (Arrays.equals(new long[] { 1, 2 }, difference(new long[] { 1, 10 }, new long[] { 2, 11 })));
        assert (Arrays.equals(new long[] { 10, 11 }, difference(new long[] { 2, 11 }, new long[] { 1, 10 })));
        assert (Arrays.equals(new long[] { 1, 2, 9, 10 }, difference(new long[] { 1, 10 }, new long[] { 2, 9 })));
        assert (Arrays.equals(new long[] { }, difference(new long[] { 2, 9 }, new long[] { 1, 10 })));
        assert (Arrays.equals(new long[] { 9, 10 }, difference(new long[] { 1, 10 }, new long[] { 0, 9 })));
        assert (Arrays.equals(new long[] { 0, 1 }, difference(new long[] { 0, 9 }, new long[] { 1, 10 })));
        assert (Arrays.equals(new long[] { 1, 5 }, difference(new long[] { 1, 5 }, new long[] { 5, 9 })));
        assert (Arrays.equals(new long[] { 5, 9 }, difference(new long[] { 5, 9 }, new long[] { 1, 5 })));
        assert (Arrays.equals(new long[] { 1, 4 }, difference(new long[] { 1, 4 }, new long[] { 5, 9 })));
        assert (Arrays.equals(new long[] { 5, 9 }, difference(new long[] { 5, 9 }, new long[] { 1, 4 })));
    }
}
