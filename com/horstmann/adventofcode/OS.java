package com.horstmann.adventofcode;
import module java.base;

public class OS {
    public static String run(List<String> command, String stdin) throws IOException, InterruptedException {
        var process = new ProcessBuilder(command).start();
        process.outputWriter().write(stdin);
        process.outputWriter().close();
        process.waitFor();
        return new String(process.getInputStream().readAllBytes());        
    }
}
