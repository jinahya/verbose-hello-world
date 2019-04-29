package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;

import static com.github.jinahya.hello.HelloWorld.SIZE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.allocateDirect;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A parameter resolver for byte buffers.
 */
class BufferParameterResolver implements ParameterResolver {

    // -----------------------------------------------------------------------------------------------------------------
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // -----------------------------------------------------------------------------------------------------------------

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
     * A marker annotation for parameters of {@link ByteBuffer} which has a backing array.
     *
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html#array()">backing array</a>
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface HasBackingArray {

    }

    // -----------------------------------------------------------------------------------------------------------------

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
     * NotEnoughRemaining} and/or {@link HasBackingArray} and returns an appropriate instance.
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
        final boolean hasBackingArray = parameter.isAnnotationPresent(HasBackingArray.class);
        final int capacity = notEnoughRemaining ? current().nextInt(SIZE) : current().nextInt(SIZE, SIZE << 1);
        return hasBackingArray ? allocate(capacity) : allocateDirect(capacity);
    }
}
