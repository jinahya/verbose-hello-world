package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({HelloWorldCdiSeTestInstanceFactory.class})
abstract class HelloWorldCdiSeTest extends HelloWorldDiTest {

}
