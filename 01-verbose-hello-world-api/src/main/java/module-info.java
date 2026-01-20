module jinahya.hello.api {
    requires static lombok;
    requires transitive org.jspecify;
    requires transitive org.slf4j;
    requires jakarta.validation;
    exports com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.api.spi;
}
