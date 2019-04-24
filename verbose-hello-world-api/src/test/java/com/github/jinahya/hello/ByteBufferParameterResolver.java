package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.*;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;

import static com.github.jinahya.hello.HelloWorld.SIZE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.allocateDirect;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * Resolves parameters of {@link ByteBuffer}.
 */
class ByteBufferParameterResolver implements ParameterResolver {

    /**
     * A marker annotation for parameters of {@link ByteBuffer} whose {@link ByteBuffer#remaining() remaining()} is less
     * than {@link HelloWorld#SIZE}.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface NotEnoughRemaining {

    }

    /**
     * A marker annotation for parameters of {@link ByteBuffer} which each allocated vis {@link
     * ByteBuffer#allocateDirect(int)}.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface Direct {

    }

    /**
     * Checks if this parameter resolver supports specified contexts.
     *
     * @param parameterContext a parameter context
     * @param extensionContext an execution context
     * @return {@code true} if {@code parameterContext.parameter.type} is equals to {@code ByteBuffer.class}.
     * @throws ParameterResolutionException if failed to resolve parameter.
     */
    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return ByteBuffer.class == parameterContext.getParameter().getType();
    }

    /**
     * Resolves a byte buffer parameter. This method checks whether the parameter is annotated {@link
     * NotEnoughRemaining} and/or {@link Direct} and returns an appropriate instance.
     *
     * @param parameterContext a parameter context
     * @param extensionContext an execution context
     * @return an instance of byte buffer
     * @throws ParameterResolutionException if failed to resolve parameter
     */
    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        final boolean notEnoughRemaining = parameter.isAnnotationPresent(NotEnoughRemaining.class);
        final boolean directBuffer = parameter.isAnnotationPresent(Direct.class);
        final int capacity = notEnoughRemaining ? current().nextInt(SIZE) : current().nextInt(SIZE, SIZE << 1);
        return directBuffer ? allocateDirect(capacity) : allocate(capacity);
    }
}
