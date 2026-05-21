package com.github.jinahya.hello.app3;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.HelloWorld;

import java.util.ServiceLoader;
import java.util.concurrent.ForkJoinPool;

class HelloWorldModule extends com.google.inject.AbstractModule {

    @com.google.inject.Provides
    HelloWorld provideHelloWorld() {
        return ServiceLoader.load(HelloWorld.class).iterator().next();
    }

    @com.google.inject.Provides
    AsynchronousHelloWorld provideAsynchronousHelloWorld(final HelloWorld service) {
        return AsynchronousHelloWorld.from(service, ForkJoinPool.commonPool());
    }
}
