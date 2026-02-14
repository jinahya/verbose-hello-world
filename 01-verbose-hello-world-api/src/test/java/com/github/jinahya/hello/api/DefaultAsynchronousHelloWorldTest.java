package com.github.jinahya.hello.api;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
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
