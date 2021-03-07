package com.caij.lox;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LoxWill {

    private ByteArrayOutputStream redirectedConsoleOutput;
    private static PrintStream oldConsole;
    private static List<String> passingTests;

    @BeforeAll
    static void setUp() {
        passingTests = new ArrayList<>();
        oldConsole = System.out;
    }

    @AfterAll
    static void tearDown() {
        // restore
        flushStdOut();
        System.setOut(oldConsole);

        System.out.println("All test have been run.");
        passingTests.forEach(test -> System.out.println(" - " + test + " has passed."));
    }

    @Test
    public void test_scenarios() throws IOException {
        final File scenarios = Paths.get("src", "test", "resources", "scenarios").toFile();
        final File[] scenariosFound = scenarios.listFiles(File::isDirectory);
        if (scenariosFound != null) {
            for (File scenario : scenariosFound) {
                redirectStdOut();
                final File input = firstFileMatching(scenario, "input.lox").orElseThrow(() -> new RuntimeException("No input found"));
                final File expected = firstFileMatching(scenario, "output").orElseThrow(() -> new RuntimeException("No expected output found."));

                final byte[] bytes = Files.readAllBytes(expected.toPath());
                final String expectedOutput = new String(bytes, Charset.defaultCharset());

                Lox.main(new String[]{input.toPath().toString()});

                assertThat(redirectedConsoleOutput.toString().trim()).isEqualTo(expectedOutput);
                flushStdOut();
                passingTests.add(scenario.getName());
            }
        }
    }

    private Optional<File> firstFileMatching(File directory, String fileNameToMatch) {
        final File[] files = directory.listFiles(f -> fileNameToMatch.equals(f.getName()));
        return files != null
                ? Optional.ofNullable(files[0])
                : Optional.empty();
    }

    private void redirectStdOut() {
        redirectedConsoleOutput = new ByteArrayOutputStream();

        // redirect
        PrintStream ps = new PrintStream(redirectedConsoleOutput);
        System.setOut(ps);
    }

    private static void flushStdOut() {
        System.out.flush();
    }
}
