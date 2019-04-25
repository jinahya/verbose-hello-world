package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A parameter resolver for files ans paths.
 */
class FileParameterResolver implements ParameterResolver {

    // -----------------------------------------------------------------------------------------------------------------
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An annotation for parameters of temporary files.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface Temporary {

        /**
         * Whether the created file should be deleted on exit by adding a shutdown hook.
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
            final Class<?> type = parameterContext.getParameter().getType();
            return File.class == type || Path.class == type;
        }
        return false;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        final Class<?> type = parameter.getType();
        final Temporary annotation = parameter.getAnnotation(Temporary.class);
        if (File.class == type) {
            final File file;
            try {
                file = File.createTempFile("tmp", null);
            } catch (final IOException ioe) {
                throw new ParameterResolutionException("failed to create a temporary file", ioe);
            }
            if (annotation.deleteOnExit()) {
                file.deleteOnExit();
            }
            return file;
        }
        if (Path.class == type) {
            final Path path;
            try {
                path = Files.createTempFile(null, null);
            } catch (final IOException ioe) {
                throw new ParameterResolutionException("failed to create a temporary file", ioe);
            }
            if (annotation.deleteOnExit()) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (!Files.deleteIfExists(path)) {
                            logger.warn("failed to delete the temporary file; {}", path);
                        }
                    } catch (final IOException ioe) {
                        throw new RuntimeException("failed to delete the temporary file: " + path, ioe);
                    }
                }));
            }
            return path;
        }
        throw new ParameterResolutionException("failed to resolve parameter; parameterContext: " + parameterContext
                                               + ", extensionContext: " + extensionContext);
    }
}
