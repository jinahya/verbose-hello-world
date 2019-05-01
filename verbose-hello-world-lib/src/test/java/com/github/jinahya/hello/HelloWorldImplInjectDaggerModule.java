package com.github.jinahya.hello;

import dagger.Module;
import dagger.Provides;

@Module(injects = {HelloWorldImplInjectDaggerTest.class})
class HelloWorldImplInjectDaggerModule {

    @Provides static HelloWorld provideHelloWorld() {
        return new HelloWorldImpl();
    }
}
