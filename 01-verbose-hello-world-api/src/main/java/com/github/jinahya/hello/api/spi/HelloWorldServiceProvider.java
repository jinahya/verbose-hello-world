package com.github.jinahya.hello.api.spi;

import com.github.jinahya.hello.api.HelloWorld;

public interface HelloWorldServiceProvider {

    boolean isQualified();

    default boolean isUnqualified() {
        return !isQualified();
    }

    HelloWorld getService();
}
