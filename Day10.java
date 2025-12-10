import static com.horstmann.adventofcode.Util.*;
import com.horstmann.adventofcode.*;
import static java.util.stream.Collectors.*;

void parse(Path path) throws IOException {
    machines = Files.lines(path).map(Machine::parse).toList();
}

record Machine(BitSet lightPattern, List<BitSet> buttons, List<Integer> joltages) {
    static Machine parse(String s) {
        var tokens = s.replaceAll("[\\[\\](){}]", "").split(" ");
        var lightPattern = new BitSet(); 
        for (int i = 0; i < tokens[0].length(); i++)
            if (tokens[0].charAt(i) == '#') lightPattern.set(i);
        var buttons = new ArrayList<BitSet>();
        for (int i = 1; i < tokens.length - 1; i++) {
            var button = new BitSet();
            for (var t : tokens[i].split(","))
                button.set(Integer.parseInt(t));            
            buttons.add(button);
        }
        var joltages = Util.parseIntegers(tokens[tokens.length - 1]);
        return new Machine(lightPattern, buttons, joltages);
    }
    
    int minimumCostLights() {
        var costs = Graphs.dijkstraCosts(new BitSet(), 
                s -> buttons.stream().map(b -> { var r = new BitSet(); r.or(s); r.xor(b); return r; }).collect(toSet()), 
                (_, _) -> 1);
        return costs.get(lightPattern);
    }
    
    CharSequence z3program() {
        var builder = new StringBuilder();
        builder.append("(reset)\n");
        for (int i = 0; i < buttons.size(); i++) {
            builder.append("(declare-const k%d Int)\n".formatted(i));
            builder.append("(assert (>= k%d 0))\n".formatted(i));
        }
        for (int j = 0; j < joltages.size(); j++) {
            builder.append("(assert (= (+");
            for (int i = 0; i < buttons.size(); i++)
                if (buttons.get(i).get(j)) builder.append(" k" + i);
            builder.append(") %d))\n".formatted(joltages.get(j)));
        }
        builder.append("(declare-const total Int)\n");
        builder.append("(assert (= total (+");
        for (int i = 0; i < buttons.size(); i++) builder.append(" k" + i);
        builder.append(")))\n");
        builder.append("(minimize total)\n");
        builder.append("(check-sat)\n");
        builder.append("(get-objectives)\n");
        return builder;
    }
    
    // https://microsoft.github.io/z3guide/
    // The Java API (https://github.com/Z3Prover/z3/tree/master/examples/java) looks ugly, 
    // so I just use their language. Here is a typical program:
    
/*

(reset)
(declare-const k0 Int)
(assert (>= k0 0))
(declare-const k1 Int)
(assert (>= k1 0))
(declare-const k2 Int)
(assert (>= k2 0))
(declare-const k3 Int)
(assert (>= k3 0))
(declare-const k4 Int)
(assert (>= k4 0))
(declare-const k5 Int)
(assert (>= k5 0))
(assert (= (+ k4 k5) 3))
(assert (= (+ k1 k5) 5))
(assert (= (+ k2 k3 k4) 4))
(assert (= (+ k0 k1 k3) 7))
(declare-const total Int)
(assert (= total (+ k0 k1 k2 k3 k4 k5)))
(minimize total)
(check-sat)
(get-objectives)
     
     */
    
    // Also call (get-models) if you need the k values
}

List<Machine> machines;

Object part1() {
    return machines.stream().mapToInt(Machine::minimumCostLights).sum();
}

Object part2() throws Exception {
    var z3program = machines.stream().map(Machine::z3program).collect(joining());
    var output = OS.run(List.of("/usr/bin/z3", "-in"), z3program);
    var result = Pattern.compile("\\(total (\\pN+)\\)")
            .matcher(output)
            .results()
            .map(r -> r.group(1))
            .mapToInt(Integer::parseInt)
            .sum();
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
