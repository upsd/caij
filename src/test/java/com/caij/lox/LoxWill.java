package com.caij.lox;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LoxWill {

    private ByteArrayOutputStream redirectedConsoleOutput;
    private PrintStream oldConsole;

    @Before
    public void setUp() {
        redirectedConsoleOutput = new ByteArrayOutputStream();
        oldConsole = System.out;

        // redirect
        PrintStream ps = new PrintStream(redirectedConsoleOutput);
        System.setOut(ps);
    }

    @After
    public void tearDown() {
        // restore
        System.out.flush();
        System.setOut(oldConsole);
    }

    @Test
    public void interpret_basic_arithmetic() throws IOException {
        final File scenarios = Paths.get("src", "test", "resources", "scenarios").toFile();
        final File[] scenariosFound = scenarios.listFiles(File::isDirectory);
        if (scenariosFound != null) {
            for (File scenario : scenariosFound) {
                final File input = firstFileMatching(scenario, "input.lox").orElseThrow(() -> new RuntimeException("No input found"));
                final File expected = firstFileMatching(scenario, "output").orElseThrow(() -> new RuntimeException("No expected output found."));

                final byte[] bytes = Files.readAllBytes(expected.toPath());
                final String expectedOutput = new String(bytes, Charset.defaultCharset());

                Lox.main(new String[]{input.toPath().toString()});

                assertThat(redirectedConsoleOutput.toString().trim()).isEqualTo(expectedOutput);
            }
        }
    }

    private Optional<File> firstFileMatching(File directory, String fileNameToMatch) {
        final File[] files = directory.listFiles(f -> fileNameToMatch.equals(f.getName()));
        return files != null
                ? Optional.ofNullable(files[0])
                : Optional.empty();
    }
}
