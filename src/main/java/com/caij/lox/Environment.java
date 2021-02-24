package com.caij.lox;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a particular environment within program, storing variables already defined
 */
public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    /**
     * Construct an environment
     */
    Environment() {
        enclosing = null;
    }

    /**
     * Construct an environment with enclosing environment
     * @param enclosing the enclosing environment
     */
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Attempts to retrieve a variable from this environment, or from an enclosing environment (if specified)
     * @param name name of variable to try and retrieve
     * @return value of variable (if found)
     * @throws RuntimeError if variable cannot be located
     */
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // if variable is not found in this environment, try the enclosing one instead
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Defines a new variable in this environment
     * @param name name of variable
     * @param value value to initialise (can be null)
     */
    void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Attempts to assign a value to a variable of this or an enclosing environment
     * @param name name of variable
     * @param value value to assign
     * @throws RuntimeError if variable cannot be located
     */
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        // if we can't find variable in this environment, try and assign to the enclosing one instead
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
