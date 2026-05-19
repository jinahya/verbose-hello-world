package com.github.jinahya.hello.api._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

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
        extends AsynchronousHelloWorldTest {

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
//        final var asynchronousService = asynchronousService();
//        Mockito.doNothing()
//                .when(asynchronousService)
//                .write(ArgumentMatchers.notNull(), ArgumentMatchers.any(), ArgumentMatchers.any());
//        final var channel = Mockito.mock(AsynchronousByteChannel.class);
//        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
//        final var handler = Mockito.mock(CompletionHandler.class);
//        // ------------------------------------------------------------------------------------ when
//        asynchronousService.write(channel, attachment, handler);
//        // ------------------------------------------------------------------------------------ then
//        Mockito.verify(asynchronousService, Mockito.times(1)).write(channel, attachment, handler);
//    }
}
