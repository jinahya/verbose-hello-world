module com.github.jinahya.hello.app4_ {
    requires transitive jakarta.cdi;
    requires transitive jakarta.inject;
    requires static lombok;
    requires transitive org.jspecify;
    requires transitive org.slf4j;
    requires jakarta.validation;
    requires com.github.jinahya.hello.api;
//    requires transitive com.github.jinahya.hello.lib;
}
