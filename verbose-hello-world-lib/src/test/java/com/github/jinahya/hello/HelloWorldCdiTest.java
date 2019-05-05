package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({HelloWorldCdiTestInstanceFactory.class})
abstract class HelloWorldCdiTest extends HelloWorldImplInjectTest {

}
