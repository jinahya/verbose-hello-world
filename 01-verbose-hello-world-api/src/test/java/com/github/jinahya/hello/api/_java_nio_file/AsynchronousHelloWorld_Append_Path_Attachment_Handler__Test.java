package com.github.jinahya.hello.api._java_nio_file;

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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.*;

@Slf4j
@SuppressWarnings({"java:S101"})
class AsynchronousHelloWorld_Append_Path_Attachment_Handler__Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    AsynchronousHelloWorld_Append_Path_Attachment_Handler__Test() {
        super(HelloWorld.class);
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __(final @TempDir Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        HelloWorldTestUtils.append_path_appends_hello_world(synchronousService());
        final var path = Files.createTempFile(tempDir, null, null);
        HelloWorldTestUtils.writeSome(path);
        final var size = Files.size(path);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>) Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.append(path, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(path, attachment);
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
