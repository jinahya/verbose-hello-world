/**
 * Concrete {@link com.github.jinahya.hello.api.HelloWorld HelloWorld} implementations layered on
 * top of {@code com.github.jinahya.hello.api}.
 */
module com.github.jinahya.hello.lib {
    requires transitive com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.lib;
}
