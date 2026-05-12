package com.github.jinahya.hello.api._java_nio_file;

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
import java.util.concurrent.TimeUnit;

/**
 * A class for testing {@link AsynchronousHelloWorld#append(Path, Object) append(path, attachment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Append_Path_Attachment_Test
        extends AsynchronousHelloWorldTest {

    @DisplayName("should throw NullPointerException when path is null")
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        final var asynchronousService = asynchronousService();
        final var path = (Path) null;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null)
        );
    }

    @DisplayName("""
            should complete the returned stage with the <attachment>
            once the synchronous append succeeds"""
    )
    @Test
    void __completed() throws Exception {
        final var asynchronousService = asynchronousService();
        final var path = Mockito.mock(Path.class);
        Mockito.doReturn(path).when(synchronousService()).append(path);
        final var attachment = new Object();
        final var future = asynchronousService.append(path, attachment);
        Assertions.assertSame(attachment, future.toCompletableFuture().get(8L, TimeUnit.SECONDS));
    }

    @DisplayName("""
            should complete the returned stage exceptionally
            when the synchronous append fails"""
    )
    @Test
    void __failed() throws IOException {
        final var asynchronousService = asynchronousService();
        final var path = Mockito.mock(Path.class);
        final var exc = new IOException("simulated append failure");
        Mockito.doThrow(exc).when(synchronousService()).append(path);
        final var attachment = new Object();
        final var future = asynchronousService.append(path, attachment);
        final var cause = Assertions.assertThrows(
                ExecutionException.class,
                () -> future.toCompletableFuture().get(8L, TimeUnit.SECONDS)
        ).getCause();
        Assertions.assertSame(exc, cause);
    }
}
