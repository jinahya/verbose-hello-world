package com.github.jinahya.hello.api;

import java.util.concurrent.Executors;

class DefaultAsynchronousHelloWorldTest
        extends AsynchronousHelloWorldTest<DefaultAsynchronousHelloWorld> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    DefaultAsynchronousHelloWorldTest() {
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
