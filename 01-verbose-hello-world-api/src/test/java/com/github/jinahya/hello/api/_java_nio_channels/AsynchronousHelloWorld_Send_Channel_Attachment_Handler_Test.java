package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.DefaultAsynchronousHelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.http.WebSocket;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#send(AsynchronousSocketChannel, Object, CompletionHandler)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("write(channel, attachment, handler)")
@Slf4j
class AsynchronousHelloWorld_Send_Channel_Attachment_Handler_Test
        extends DefaultAsynchronousHelloWorldTest {

//    /**
//     * Verifies that the
//     * {@link AsynchronousHelloWorld#sendBinary(WebSocket, boolean) send(channel, attachment,
//     * handler)} method invokes
//     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)}
//     * method with given arguments.
//     */
//    @DisplayName("should invoke write(channel, attachment, handler>")
//    @Test
//    @SuppressWarnings({"unchecked"})
//    void __() {
//        // ----------------------------------------------------------------------------------- given
//        final var service = asynchronousService();
//        Mockito.doNothing()
//                .when(service)
//                .write(ArgumentMatchers.notNull(), ArgumentMatchers.any(), ArgumentMatchers.any());
//        final var channel = Mockito.mock(AsynchronousByteChannel.class);
//        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
//        final var handler = Mockito.mock(CompletionHandler.class);
//        // ------------------------------------------------------------------------------------ when
//        service.write(channel, attachment, handler);
//        // ------------------------------------------------------------------------------------ then
//        Mockito.verify(service, Mockito.times(1)).write(channel, attachment, handler);
//    }
}
