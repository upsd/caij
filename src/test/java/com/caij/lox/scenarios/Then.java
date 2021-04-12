package com.caij.lox.scenarios;

import java.io.File;

public class Then {

    private final File input;

    public Then(File input) {
        this.input = input;
    }

    public Scenario outputWillBe(String expectedOutput) {
        return new Scenario(input, expectedOutput);
    }
}
