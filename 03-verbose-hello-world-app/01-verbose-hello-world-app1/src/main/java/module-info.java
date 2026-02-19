module com.github.jinahya.hello.app1_ {
    requires static lombok;
    requires transitive org.jspecify;
    requires transitive org.slf4j;
//    requires jakarta.validation;
    requires com.github.jinahya.hello.api;
    requires com.github.jinahya.hello.lib;
}
