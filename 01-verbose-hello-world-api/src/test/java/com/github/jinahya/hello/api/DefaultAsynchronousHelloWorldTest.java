package com.github.jinahya.hello.api;

import java.util.concurrent.Executors;

public class DefaultAsynchronousHelloWorldTest
        extends AsynchronousHelloWorldTest<DefaultAsynchronousHelloWorld> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    protected DefaultAsynchronousHelloWorldTest() {
        super(DefaultAsynchronousHelloWorld.class);
    }

    // ------------------------------------------------------------------ AsynchronousHelloWorldTest
    @Override
    DefaultAsynchronousHelloWorld newTypeInstance() {
        return new DefaultAsynchronousHelloWorld(
                new HelloWorldRevisited() {
                }
        );
    }

    // ---------------------------------------------------------------------------------------------
}
