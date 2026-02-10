module com.github.jinahya.hello.api {
    requires static lombok;
    requires transitive org.jspecify;
    requires transitive org.slf4j;
    requires jakarta.validation;
    requires org.reactivestreams;
    requires static jdk.httpserver;
    requires static java.net.http;
    requires java.sql;
    requires java.sql.rowset;
    exports com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.api.spi;
    exports com.github.jinahya.hello.api.reactive;
}
