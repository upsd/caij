package com.caij.lox;

import com.caij.lox.scenarios.Given;
import com.caij.lox.scenarios.Scenario;
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
    private static int numberOfScenarios;
    private static PrintStream oldConsole;
    private static List<Scenario> passingScenarios;

    @BeforeAll
    static void setUp() {
        numberOfScenarios = 0;
        passingScenarios = new ArrayList<>();
        oldConsole = System.out;
    }

    @AfterAll
    static void tearDown() {
        // restore
        flushStdOut();
        System.setOut(oldConsole);

        if (!passingScenarios.isEmpty()) {
            System.out.println(passingScenarios.size() + " / " + numberOfScenarios + " have run successfully:");
            passingScenarios.forEach(scenario -> System.out.println(" - " + scenario.getTitle() + " has passed."));
        }
    }

    @Test
    void test_scenarios() throws IOException {
        final File whereScenariosShouldBe = Paths.get("src", "test", "resources", "scenarios").toFile();
        final File[] allScenarioFolders = whereScenariosShouldBe.listFiles(File::isDirectory);
        if (allScenarioFolders != null) {
            final List<Scenario> scenariosToTest = scenariosFrom(allScenarioFolders);
            numberOfScenarios = scenariosToTest.size();
            testAll(scenariosToTest);
        }
    }

    private void testAll(List<Scenario> scenariosToTest) throws IOException {
        for (Scenario scenario : scenariosToTest) {
            redirectStdOut();

            Lox.main(new String[]{scenario.getInput().toPath().toString()});

            assertThat(redirectedConsoleOutput.toString().trim())
                    .as(scenario.getTitle() + " has failed.")
                    .isEqualTo(scenario.getExpectedOutput());

            flushStdOut();
            passingScenarios.add(scenario);
        }
    }

    private List<Scenario> scenariosFrom(File[] scenarioFolders) throws IOException {
        final List<Scenario> scenariosToTest = new ArrayList<>();
        for (File scenario : scenarioFolders) {
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
