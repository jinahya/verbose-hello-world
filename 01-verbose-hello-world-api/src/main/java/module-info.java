module jinahya.verbose.hello.world.api {
    requires static lombok;
    requires transitive org.jspecify;
    requires transitive org.slf4j;
    exports com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.api.spi;
}
