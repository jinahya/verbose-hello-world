package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#append(Executor, Path, Object) append(executor, path, attachment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(executor, path, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Append_Executor_Path_Attachment_Test
        extends AsynchronousHelloWorldTest {

    @DisplayName("should throw NullPointerException when executor is null")
    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        final AsynchronousHelloWorld service = asynchronousService();
        final var executor = (Executor) null;
        final var path = Mockito.mock(Path.class);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(executor, path, null)
        );
    }

    @DisplayName("should throw NullPointerException when path is null")
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        final AsynchronousHelloWorld service = asynchronousService();
        final var executor = (Executor) Runnable::run;
        final var path = (Path) null;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(executor, path, null)
        );
    }

    @DisplayName("""
            should complete the returned future with the <attachment>
            once the synchronous append on the executor succeeds"""
    )
    @Test
    void __completed() throws Exception {
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var executor = (Executor) Runnable::run;
        final var path = Mockito.mock(Path.class);
        Mockito.doReturn(path).when(service()).append(path);
        final var attachment = new Object();
        final var future = service.append(executor, path, attachment);
        Assertions.assertSame(attachment, future.get(8L, TimeUnit.SECONDS));
    }

    @DisplayName("""
            should complete the returned future exceptionally
            when the synchronous append on the executor fails"""
    )
    @Test
    void __failed() throws Exception {
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var executor = (Executor) Runnable::run;
        final var path = Mockito.mock(Path.class);
        final var exc = new IOException("simulated append failure");
        Mockito.doThrow(exc).when(service()).append(path);
        final var attachment = new Object();
        final var future = service.append(executor, path, attachment);
        final var cause = Assertions.assertThrows(
                ExecutionException.class,
                () -> future.get(8L, TimeUnit.SECONDS)
        ).getCause();
        // applyAsync wraps the IOException in UncheckedIOException
        Assertions.assertSame(exc, cause.getCause());
    }
}
