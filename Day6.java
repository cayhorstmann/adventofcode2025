import static com.horstmann.adventofcode.Util.*;

List<List<Integer>> inputs;
List<String> operators;
List<String> lines;

void parse(Path path) throws IOException {
    inputs = new ArrayList<>();
    
    lines = Files.readAllLines(path);
    for (String line : lines.subList(0, lines.size() - 1)) {
        inputs.add(parseIntegers(line));        
    }
    operators = new ArrayList<>(List.of(lines.getLast().split("\\s+")));
}

long evaluate(int column, String operator) {
    long result = inputs.get(0).get(column);
    for (int i = 1; i < inputs.size(); i++) {
        var operand = inputs.get(i).get(column);
        if (operator.equals("+")) 
            result += operand;
        else
            result *= operand;
    }
    return result;
}

Object part1() {
    long sum = 0;
    for (int i = 0; i < operators.size(); i++) {
        sum += evaluate(i, operators.get(i));
    }            
    return sum;
}

Object part2() {
    String operatorRow = lines.getLast();
    int column = 0;
    int nextColumn = -1;
    long sum = 0;
    while (column < operatorRow.length()) {
        char operator = operatorRow.charAt(column);
        nextColumn = column + 1;
        while (nextColumn < operatorRow.length() && operatorRow.charAt(nextColumn) == ' ')
            nextColumn++;
        if (nextColumn == operatorRow.length()) nextColumn++;
        int[] operands = new int[nextColumn - column - 1];
        for (int c = 0; c < operands.length; c++) {
            String digits = "";
            for (int r = 0; r < lines.size() - 1; r++) {
                digits += lines.get(r).charAt(column + c);
            }
            operands[c] = Integer.parseInt(digits.strip());
        }
        long result = operands[0];
        for (int i = 1; i < operands.length; i++)
            if (operator == '+')
                result += operands[i];
            else
                result *= operands[i];
        sum += result;
        column = nextColumn;
    }
    return sum;
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
