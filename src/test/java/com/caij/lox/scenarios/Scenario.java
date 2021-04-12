package com.caij.lox.scenarios;

import java.io.File;

public class Scenario {
    private final File input;
    private final String expectedOutput;
    private String title;

    private Scenario(String title, File input, String expectedOutput) {
        this.title = title;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public Scenario(File input, String expectedOutput) {
        this("", input, expectedOutput);
    }

    public Scenario named(String name) {
        this.title = name;
        return this;
    }

    public File getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public String getTitle() {
        return title;
    }
}
