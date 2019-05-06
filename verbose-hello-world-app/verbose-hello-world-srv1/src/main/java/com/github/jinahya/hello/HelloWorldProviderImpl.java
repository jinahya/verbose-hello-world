package com.github.jinahya.hello;

public class HelloWorldProviderImpl implements HelloWorldProvider {

    @Override
    public HelloWorld getAvailable() {
        try {
            return Class.forName("com.github.jinahya.hello.HelloWorldImpl").asSubclass(HelloWorld.class)
                    .getConstructor().newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate HelloWorldImpl", roe);
        }
    }
}
