package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;

import java.util.Objects;

public abstract class AbstractReactiveHelloWorld implements ReactiveHelloWorldFactory {

    protected AbstractReactiveHelloWorld(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    protected final HelloWorld service;
}
