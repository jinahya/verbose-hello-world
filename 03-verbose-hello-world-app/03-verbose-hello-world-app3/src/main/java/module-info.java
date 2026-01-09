module jinahya.hello.app3 {
    requires jinahya.hello.api;
    requires com.google.guice;
    requires jakarta.inject;
    uses com.github.jinahya.hello.api.HelloWorld;
    opens com.github.jinahya.hello.app to com.google.guice;
}
