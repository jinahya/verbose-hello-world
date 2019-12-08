package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.hibernate.validator.testutil.ValidationInvocationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.InvocationHandler;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.nio.channels.AsynchronousFileChannel.open;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
public class AsynchronousHelloWorldTest {

    // ------------------------------------------------------------------------------------------------ AsyncFileChannel
    @Test
    void testAppend(@TempDir final Path tempDir) throws Exception {
        final Path path = Files.createTempFile(tempDir, null, null);
        try (AsynchronousFileChannel channel = open(path, StandardOpenOption.WRITE)) {
            log.debug("channel: {}", channel);
            log.debug("helloWorld: {}", helloWorld);
            helloWorld.append(channel);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    AsynchronousHelloWorld helloWorld() {
        final ClassLoader loader = getClass().getClassLoader();
        final Class<?>[] interfaces = new Class<?>[] {AsynchronousHelloWorld.class};
        final Validator validator
                = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();
        final InvocationHandler handler = new ValidationInvocationHandler(helloWorld, validator);
        return (AsynchronousHelloWorld) newProxyInstance(loader, interfaces, handler);
    }

    /**
     * Stubs {@link HelloWorld#set(byte[], int)} method of {@link Spy spied} {@code helloWorld} instance to return
     * specified {@code array}.
     */
    @BeforeEach
    private void stubSetArrayIndexToReturnSpecifiedArray() {
        when(helloWorld.set(any(byte[].class), anyInt())) // <1>
                .thenAnswer(i -> i.getArgument(0));       // <2>
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    AsynchronousHelloWorld helloWorld;
}
