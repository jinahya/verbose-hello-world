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

import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * A class for exploring
 * {@link AsynchronousHelloWorld#append(Path, Object) append(path, attachment)} method with real
 * implementations.
 *
 * @param <T> the subtype of {@link AsynchronousHelloWorld}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, attachment)")
@Slf4j
@SuppressWarnings({"java:S101"})
abstract class AsynchronousHelloWorld_Append_Path_Attachment__Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    AsynchronousHelloWorld_Append_Path_Attachment__Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method appends {@code hello-world-bytes} to a real {@link Path}, and
     * completes the returned stage with the {@code attachment}.
     *
     * @param tempDir the temporary directory.
     * @throws Exception if an error occurs.
     */
    @DisplayName("""
            should append <hello-world-bytes> to a real <Path>,
            and complete the returned stage with the <attachment>""")
    @Test
    void __(final @TempDir Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        HelloWorld__TestUtils.append_path_appends_hello_world(synchronousService());
        final var path = Files.createTempFile(tempDir, null, null);
        HelloWorld__TestUtils.writeSome(path);
        final var size = Files.size(path);
        final var attachment = new Object();
        // ---------------------------------------------------------------------------------- when
        final var future = asynchronousService.append(path, attachment);
        // ---------------------------------------------------------------------------------- then
        Assertions.assertSame(attachment, future.toCompletableFuture().get(8L, TimeUnit.SECONDS));
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
