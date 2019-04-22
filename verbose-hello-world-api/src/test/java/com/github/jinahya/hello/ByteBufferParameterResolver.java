package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;

import static java.util.concurrent.ThreadLocalRandom.current;

class ByteBufferParameterResolver implements ParameterResolver {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface NotEnoughRemaining {

    }

//    @Retention(RetentionPolicy.RUNTIME)
//    @Target({ElementType.PARAMETER})
//    @interface EnoughRemaining {
//
//    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface DirectBuffer {

    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return ByteBuffer.class == parameterContext.getParameter().getType();
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        if (parameter.isAnnotationPresent(NotEnoughRemaining.class)) {
            final int capacity = current().nextInt(HelloWorld.SIZE);
            if (parameter.isAnnotationPresent(DirectBuffer.class)) {
                return ByteBuffer.allocateDirect(capacity);
            } else {
                return ByteBuffer.allocate(capacity);
            }
        }
        final int capacity = current().nextInt(HelloWorld.SIZE, HelloWorld.SIZE << 1);
        if (parameter.isAnnotationPresent(DirectBuffer.class)) {
            return ByteBuffer.allocateDirect(capacity);
        } else {
            return ByteBuffer.allocate(capacity);
        }
    }
}
