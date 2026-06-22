package com.github.jinahya.hello.api;

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

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.reactivestreams.tck.flow.junit.jupiter.*;
import org.reactivestreams.tck.junit.jupiter.*;

import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Drives the Reactive Streams 1.0 {@link FlowPublisherVerification TCK} (JUnit Jupiter port,
 * {@link java.util.concurrent.Flow Flow}-flavoured) against {@link HelloWorldArrayPublisher}
 * <em>naively</em> — {@link #createFlowPublisher(long)} returns a fresh publisher with no
 * length-coercing wrapper.
 * <p>
 * {@link HelloWorldArrayPublisher} is open-ended (it never naturally completes), so
 * {@link #maxElementsFromPublisher()} returns {@link Long#MAX_VALUE}, which tells the TCK to skip
 * every test that requires a natural {@code onComplete}.
 * <p>
 * {@link #createFailedFlowPublisher()} returns {@code null}; the additional "failed publisher"
 * tests are skipped.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldArrayPublisher
 * @see FlowPublisherVerification
 */
@DisplayName("HelloWorld / array Publisher / TCK")
class HelloWorld_Array_Publisher_TckTest extends FlowPublisherVerification<byte[]> {

    HelloWorld_Array_Publisher_TckTest() {
        super(new TestEnvironment());
    }

    /**
     * {@inheritDoc}
     *
     * @param elements ignored — {@link HelloWorldArrayPublisher} is open-ended.
     * @return a fresh {@link HelloWorldArrayPublisher} backed by a fresh
     * {@code hello-world-bytes}-stubbed mock {@link HelloWorld} service.
     */
    @Override
    public Flow.Publisher<byte[]> createFlowPublisher(final long elements) {
        final var service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        set_array_sets_hello_world_bytes(service);
        return new HelloWorldArrayPublisher(service);
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code null}.
     */
    @Override
    public Flow.Publisher<byte[]> createFailedFlowPublisher() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link Long#MAX_VALUE} — {@link HelloWorldArrayPublisher} never naturally completes.
     */
    @Override
    public long maxElementsFromPublisher() {
        return Long.MAX_VALUE;
    }
}
