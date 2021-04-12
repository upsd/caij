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
    private static List<Scenario> passingScenarios;

    @BeforeAll
    static void setUp() {
        passingScenarios = new ArrayList<>();
        oldConsole = System.out;
    }

    @AfterAll
    static void tearDown() {
        // restore
        flushStdOut();
        System.setOut(oldConsole);

        if (!passingScenarios.isEmpty()) {
            System.out.println("All tests have been run (" + passingScenarios.size() + "):");
            passingScenarios.forEach(scenario -> System.out.println(" - " + scenario.getTitle() + " has passed."));
        }
    }

    @Test
    void test_scenarios() throws IOException {
        final File scenarios = Paths.get("src", "test", "resources", "scenarios").toFile();
        final File[] scenariosFound = scenarios.listFiles(File::isDirectory);
        if (scenariosFound != null) {
            final List<Scenario> scenariosToTest = getScenarios(scenariosFound);

            for (Scenario scenario : scenariosToTest) {
                redirectStdOut();

                Lox.main(new String[]{scenario.getInput().toPath().toString()});

                assertThat(redirectedConsoleOutput.toString().trim()).isEqualTo(scenario.getExpectedOutput());
                flushStdOut();
                passingScenarios.add(scenario);
            }

        }
    }

    private List<Scenario> getScenarios(File[] scenariosFound) throws IOException {
        final List<Scenario> scenariosToTest = new ArrayList<>();
        for (File scenario : scenariosFound) {
            final File input = firstFileMatching(scenario, "input.lox").orElseThrow(() -> new RuntimeException("No input found"));
            final File expected = firstFileMatching(scenario, "output").orElseThrow(() -> new RuntimeException("No expected output found."));
            scenariosToTest.add(Given.input(input).outputWillBe(contentsOf(expected)).named(scenario.getName()));
        }
        return scenariosToTest;
    }

    private String contentsOf(File expected) throws IOException {
        final byte[] bytes = Files.readAllBytes(expected.toPath());
        return new String(bytes, Charset.defaultCharset());
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
