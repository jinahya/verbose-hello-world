package com.github.jinahya.hello.api;

import java.util.Objects;

abstract class HelloWorldWrapper {

    HelloWorldWrapper(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    final HelloWorld service;
}
