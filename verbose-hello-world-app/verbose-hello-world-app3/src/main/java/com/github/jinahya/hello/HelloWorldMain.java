package com.github.jinahya.hello;

import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A class whose {@link #main(String[])} method prints {@code hello, world} to {@link System#out}.
 */
public class HelloWorldMain {

    private static final Logger logger = getLogger(lookup().lookupClass());

    /**
     * The main method of this program which prints {@code hello, world} followed by a new line character.
     *
     * @param args an array of command line arguments
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

    @Injectg
    HelloWorld helloWorld;
}
