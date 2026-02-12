package com.github.jinahya.hello.api;

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
