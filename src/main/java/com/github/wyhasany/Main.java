package com.github.wyhasany;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Main {

    public static void main(String[] args) {
        try {
            var startupJson = Files.readString(Path.of(args[0]));
            var converter = new SpringStartupToCollapseStackConverter();
            var collapsed = converter.parse(startupJson);
            Files.writeString(Path.of(args[1]), collapsed, UTF_8, TRUNCATE_EXISTING, CREATE);
        } catch (IOException e) {
            handleIoException(e);
        }
    }

    private static void handleIoException(IOException e) {
        System.out.println(
            """
            Usage: java -jar converter.jar <input> <output>
                        
            Example command:
              java -jar input.json output.collapsed
            """);
        e.printStackTrace();
    }
}
