package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;

class TemporaryFileParameterResolver implements ParameterResolver {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A marker annotation for temporary file parameters.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface Temporary {

        /**
         * Whether the created file should be deleted on exit.
         *
         * @return {@code true} if the file should be deleted on exit; {@code false} otherwise.
         */
        boolean deleteOnExit() default true;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        if (parameterContext.isAnnotated(Temporary.class)) {
            final Parameter parameter = parameterContext.getParameter();
            final Class<?> parameterType = parameter.getType();
            return File.class == parameterType || Path.class == parameterType;
        }
        return false;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        final Temporary temporary = parameter.getAnnotation(Temporary.class);
        final Class<?> parameterType = parameter.getType();
        if (File.class == parameterType) {
            final File file;
            try {
                file = File.createTempFile("tmp", null);
            } catch (final IOException ioe) {
                throw new ParameterResolutionException("failed to create a temporary file", ioe);
            }
            if (temporary.deleteOnExit()) {
                file.deleteOnExit();
            }
            return file;
        }
        if (Path.class == parameterType) {
            final Path path;
            try {
                path = Files.createTempFile(null, null);
            } catch (final IOException ioe) {
                throw new ParameterResolutionException("failed to create a temporary file", ioe);
            }
            if (temporary.deleteOnExit()) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (final IOException ioe) {
                        throw new RuntimeException("failed to delete the temporary file: " + path, ioe);
                    }
                }));
            }
            return path;
        }
        throw new ParameterResolutionException("failed to resolve parameter for " + parameterContext);
    }
}
