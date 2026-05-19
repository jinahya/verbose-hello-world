import com.github.jinahya.hello.api.spi.HelloWorldServiceProvider;

module com.github.jinahya.hello.lib {
    requires static lombok;
    requires transitive com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.lib;
    uses HelloWorldServiceProvider;
}
