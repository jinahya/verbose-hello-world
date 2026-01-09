module jinahya.hello.lib {
    requires static lombok;
    requires transitive jinahya.hello.api;
    exports com.github.jinahya.hello.lib;
    uses com.github.jinahya.hello.api.spi.HelloWorldServiceProvider;
}
