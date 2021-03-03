package com.caij.lox;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        final String pathToTestFile = Paths.get("src","test","resources", "scenarios/basic_arithmetic/input.lox").toString();
        final Path pathToExpectedOutputFile = Paths.get("src","test","resources", "scenarios/basic_arithmetic/output");
        final byte[] bytes = Files.readAllBytes(pathToExpectedOutputFile);
        final String expectedOutput = new String(bytes, Charset.defaultCharset());

        Lox.main(new String[]{pathToTestFile});

        assertThat(redirectedConsoleOutput.toString().trim()).isEqualTo(expectedOutput);
    }
}
