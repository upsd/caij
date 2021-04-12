package com.caij.lox.scenarios;

import java.io.File;

public class Given {

    public static Then input(File input) {
        return new Then(input);
    }
}
