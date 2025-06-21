import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (
                BufferedReader br = new BufferedReader(new FileReader("input_cfg.txt"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("output_cfg.txt"))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int problemNumber = Integer.parseInt(line);
                bw.write(problemNumber + "\n");
                StringWriter sw = new StringWriter();
                BufferedWriter tempBw = new BufferedWriter(sw);
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null && !line.trim().equals("end")) {
                    sb.append(line).append("\n");
                }
                BufferedReader problemReader = new BufferedReader(new StringReader(sb.toString()));
                switch (problemNumber) {
                    case 1 -> new Problem1(problemReader, tempBw);
                    case 2 -> new Problem2(problemReader, tempBw);
                    case 3 -> new Problem3(problemReader, tempBw);
                    case 4 -> new Problem4(problemReader, tempBw);
                    case 5 -> new Problem5(problemReader, tempBw);
                    default -> {
                        tempBw.write("Invalid problem number\n");
                        tempBw.flush();
                    }
                }
                tempBw.flush();
                bw.write(sw.toString());
                bw.write("x\n");
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class CFGClass {
    private final List<Character> terminals;
    private final List<Character> nonTerminals;
    private final char startSymbol;
    private final Map<Character, List<String>> productions;

    public CFGClass(List<Character> terminals, List<Character> nonTerminals,
                    char startSymbol, Map<Character, List<String>> productions) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.startSymbol = startSymbol;
        this.productions = productions;
    }

    private String getTerminalPrefix(String s) {
        StringBuilder prefix = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (terminals.contains(c)) prefix.append(c);
            else break;
        }
        return prefix.toString();
    }

    private int countNonTerminals(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (nonTerminals.contains(c)) count++;
        }
        return count;
    }

    public boolean derive(String text) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(String.valueOf(startSymbol));
        visited.add(String.valueOf(startSymbol));

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(text)) return true;

            String currentTerminalPrefix = getTerminalPrefix(current);
            if (!text.startsWith(currentTerminalPrefix)) continue;

            for (int i = 0; i < current.length(); i++) {
                char symbol = current.charAt(i);
                if (nonTerminals.contains(symbol)) {
                    List<String> rules = productions.getOrDefault(symbol, List.of());
                    for (String rule : rules) {
                        String replaced = rule.equals("ε") ? "" : rule;
                        String newString = current.substring(0, i) + replaced + current.substring(i + 1);
                        String newTerminalPrefix = getTerminalPrefix(newString);
                        if (!text.startsWith(newTerminalPrefix)) continue;

                        if (!visited.contains(newString)) {
                            visited.add(newString);
                            queue.add(newString);
                        }
                    }
                    break; // Leftmost derivation only
                }
            }
        }
        return false;
    }

    public void solveProblem(BufferedReader br, BufferedWriter bw) {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String input = line.trim().replace("ε", "");
                if (input.isEmpty() && !terminals.isEmpty()) {
                    input = "";
                }
                boolean result = derive(input);
                bw.write(result ? "accepted\n" : "not accepted\n");
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Problem1 {
    public Problem1(BufferedReader br, BufferedWriter bw) {
        List<Character> terminals = List.of('a', 'b');
        List<Character> nonTerminals = List.of('S');
        Map<Character, List<String>> rules = Map.of(
                'S', List.of("aSbS", "bSaS", "ε")
        );
        new CFGClass(terminals, nonTerminals, 'S', rules).solveProblem(br, bw);
    }
}

class Problem2 {
    public Problem2(BufferedReader br, BufferedWriter bw) {
        List<Character> terminals = List.of('a', 'b');
        List<Character> nonTerminals = List.of('S');
        Map<Character, List<String>> rules = Map.of(
                'S', List.of("ε", "aSbbS", "bbSaS", "aSbb", "abSb", "bSab", "baSb", "bSba", "bbSa")
        );
        new CFGClass(terminals, nonTerminals, 'S', rules).solveProblem(br, bw);
    }
}

class Problem3 {
    public Problem3(BufferedReader br, BufferedWriter bw) {
        List<Character> terminals    = List.of('a', 'b');
        List<Character> nonTerminals = List.of('S', 'A');
        Map<Character, List<String>> rules = Map.of(
                'S', List.of(
                        "aAb",  // start a, end b, interior any via A
                        "bAa",  // start b, end a
                        "aSb",  // start=a, end=b but recurse on interior
                        "bSa",   // start=b, end=a
                        "aSa",
                        "bSb"
                ),
                // A is a simple right‐linear generator for (a|b)*, possibly empty
                'A', List.of(
                        "aA",
                        "bA",
                        "ε"
                )
        );

        new CFGClass(terminals, nonTerminals, 'S', rules)
                .solveProblem(br, bw);
    }
}

class Problem4 {
    public Problem4(BufferedReader br, BufferedWriter bw) {
        List<Character> terminals = List.of('a', 'b');
        List<Character> nonTerminals = List.of('S', 'B');
        Map<Character, List<String>> rules = Map.of(
                'S', List.of("aaaB"),
                'B', List.of("aaBb", "ε")
        );
        new CFGClass(terminals, nonTerminals, 'S', rules).solveProblem(br, bw);
    }
}

class Problem5 {
    public Problem5(BufferedReader br, BufferedWriter bw) {
        List<Character> terminals = List.of('a', 'b');
        List<Character> nonTerminals = List.of('S');
        Map<Character, List<String>> rules = Map.of(
                'S', List.of("aS", "aSb", "a")
        );
        new CFGClass(terminals, nonTerminals, 'S', rules).solveProblem(br, bw);
    }
}