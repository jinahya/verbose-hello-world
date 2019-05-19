package com.github.jinahya.hello;

import java.io.IOException;
import java.util.logging.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.logging.Logger.getLogger;

/**
 * A class whose {@link #main(String[])} method prints {@code hello, world} to {@link System#out}.
 */
public class HelloWorldMain {

    private static final Logger logger = getLogger(lookup().lookupClass().getName());

    /**
     * The main method of this program which prints {@code hello, world} followed by a new line character.
     *
     * @param args an array of main arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String[] args) throws IOException {
        // TODO: implement!
    }

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
