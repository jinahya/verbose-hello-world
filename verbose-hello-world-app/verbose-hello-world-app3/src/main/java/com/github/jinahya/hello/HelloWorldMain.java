package com.github.jinahya.hello;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * A class whose {@link #main(String[])} method prints {@code hello, world} to {@link System#out}.
 */
public class HelloWorldMain {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * The main method of this program which prints {@code hello, world} followed by a line delimiter.
     *
     * @param args an array of main arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String[] args) throws IOException {
        // TODO: implement!
    }

    @Inject
    private HelloWorld helloWorld;
}
