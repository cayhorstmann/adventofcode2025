import static com.horstmann.adventofcode.Util.*;

List<String> banks;

void parse(Path path) throws IOException {
    banks = Files.readAllLines(path);
}

// Not needed any more but it explains how the general case works
int highest2(String bank) {
    // Find largest digit but not in last place
    int imax = 0;
    for (int i = 1; i < bank.length() - 1; i++)
        if (bank.charAt(i) > bank.charAt(imax)) imax = i;
    // Find largest digit after
    int jmax = imax + 1;
    for (int j = jmax + 1; j < bank.length(); j++)
        if (bank.charAt(j) > bank.charAt(jmax)) jmax = j;
    return 10 * (bank.charAt(imax) - '0') + bank.charAt(jmax) - '0';
}

long highest(String bank, int digits) {
    long result = 0;
    int jmax = -1;
    for (int d = 0; d < digits; d++) {
        jmax = jmax + 1;
        for (int j = jmax + 1; j < bank.length() - digits + d + 1; j++)
            if (bank.charAt(j) > bank.charAt(jmax)) jmax = j;
        result = 10 * result + bank.charAt(jmax) - '0';
    }
    return result;
}

Object part1() {
    return banks.stream().mapToLong(this::highest2).sum();
}

Object part2() {
    return banks.stream().mapToLong(b -> highest(b, 12)).sum();
}

void main() throws Exception {
    parse(inputPath("a"));
    time(this::part1);
    time(this::part2);
    parse(inputPath("z"));
    time(this::part1);
    time(this::part2);
}
