package com.github.jinahya.jupiter.api.extension;

import com.github.jinahya.jupiter.api.io.TempFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;

/**
 * A class for resolving parameters of temporary file to test with.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see TempFile
 */
@Slf4j
public class TempFileParameterResolver implements ParameterResolver, AfterEachCallback {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        final Class<?> type = parameter.getType();
        return parameter.isAnnotationPresent(TempFile.class) && (type == File.class || type == Path.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        method = extensionContext.getTestMethod().orElse(null);
//        final Path file;
        try {
            file = createTempFile(null, null);
            log.debug("temp file created: {}", file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
//        getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                final boolean deleted = deleteIfExists(file);
//                log.debug("temp file deleted: {} {}", deleted, file);
//            } catch (final IOException ioe) {
//                throw new RuntimeException(ioe);
//            }
//        }));
        if (parameterContext.getParameter().getType() == File.class) {
            return file.toFile();
        }
        return file;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        if (method == null || !method.equals(extensionContext.getTestMethod().orElse(null))) {
            return;
        }
        try {
            final boolean deleted = deleteIfExists(file);
            log.debug("temp file deleted: {} {}", deleted, file);
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private transient Method method;

    private transient Path file;
}
