package com.github.jinahya.hello;

import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.extension.ExtendWith;

@AddBeanClasses({HelloWorldImplInjectWeldFactory.class})
@ExtendWith({WeldJunit5Extension.class})
class HelloWorldImplInjectWeldTest extends HelloWorldImplInjectTest {

}
