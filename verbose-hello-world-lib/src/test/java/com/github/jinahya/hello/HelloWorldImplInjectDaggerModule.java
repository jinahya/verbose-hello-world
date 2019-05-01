package com.github.jinahya.hello;

import dagger.Module;
import dagger.Provides;

@Module(injects = {HelloWorldImplInjectDaggerTest.class})
class HelloWorldImplInjectDaggerModule {

    @Provides
    public HelloWorld provideHelloWorld() {
        return new HelloWorldImpl();
    }
}
