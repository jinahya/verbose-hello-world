package com.github.jinahya.hello;

import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.extension.ExtendWith;

@AddBeanClasses({HelloWorldCdiFactory.class})
@ExtendWith({WeldJunit5Extension.class})
class HelloWordWeldJunit5Test extends HelloWorldDiTest {

}
