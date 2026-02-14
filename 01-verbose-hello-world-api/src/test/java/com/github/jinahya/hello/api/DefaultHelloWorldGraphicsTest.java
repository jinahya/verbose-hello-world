package com.github.jinahya.hello.api;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class DefaultHelloWorldGraphicsTest
        extends HelloWorldGraphicsTest<DefaultHelloWorldGraphics> {

    DefaultHelloWorldGraphicsTest() {
        super(DefaultHelloWorldGraphics.class);
    }

    @Override
    DefaultHelloWorldGraphics newTypeInstance() {
        return new DefaultHelloWorldGraphics(
                new HelloWorldRevisited() {
                }
        );
    }
}
