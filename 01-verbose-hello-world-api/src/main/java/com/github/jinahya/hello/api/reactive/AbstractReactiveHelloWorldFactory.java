package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;

import java.util.Objects;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class AbstractReactiveHelloWorldFactory
        implements ReactiveHelloWorldFactory {

    protected AbstractReactiveHelloWorldFactory(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    protected final HelloWorld service;
}
