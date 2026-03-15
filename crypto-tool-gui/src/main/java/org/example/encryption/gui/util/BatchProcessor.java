package org.example.encryption.gui.util;

import java.util.function.Function;

public class BatchProcessor {

    public static String process(String multiLineInput, Function<String, String> operation) {
        StringBuilder result = new StringBuilder();
        String[] lines = multiLineInput.split("\n");
        int index = 1;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            try {
                String output = operation.apply(trimmed);
                result.append("[").append(index).append("] ").append(output).append("\n");
            } catch (Exception e) {
                result.append("[").append(index).append("] [错误] ").append(e.getMessage()).append("\n");
            }
            index++;
        }
        return result.toString();
    }
}
