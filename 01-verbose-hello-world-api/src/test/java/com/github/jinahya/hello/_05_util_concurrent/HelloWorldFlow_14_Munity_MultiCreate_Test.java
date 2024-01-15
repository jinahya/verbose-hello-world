package com.github.jinahya.hello._05_util_concurrent;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import com.github.jinahya.hello.HelloWorldFlow;
import com.github.jinahya.hello._HelloWorldTestUtils;
import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@DisplayName("MultiCreate")
@Slf4j
class HelloWorldFlow_14_Munity_MultiCreate_Test extends _HelloWorldFlow_Test {

    @DisplayName("publisher(Flow.Publisher)")
    @Nested
    class PublisherTest {

        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR)
            );
            final var multi = Multi.createFrom().publisher(publisher);
            // -------------------------------------------------------------------------------- when
            final var cancellable = multi
                    .subscribe()
                    .with(i -> {
                        log.debug("item: {}", (char) i.byteValue());
                    });
            // -------------------------------------------------------------------------------- then
            _HelloWorldTestUtils.awaitForOneSecond();
            // verify, publisher.subscribe(non-null) invoked, once
            Mockito.verify(publisher, Mockito.times(1)).subscribe(ArgumentMatchers.notNull());
        }

        @Test
        void __byte() {
            interface AlienService { // @formatter:off
                default void doWithMulti(final Multi<? extends Byte> multi) {
                    multi.subscribe().with(i -> {
                        log.debug("item: {}", (char) i.byteValue());
                    });
                }
            } // @formatter:on
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR);
            final var multi = Multi.createFrom().publisher(publisher);
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doWithMulti(multi);
        }

        @Test
        void __string() {
            interface AlienService { // @formatter:off
                default void doWithMulti(final Multi<? extends String> multi) {
                    multi.select().first(3).subscribe().with(i -> {
                        log.debug("item: {}", i);
                    });
                }
            } // @formatter:on
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var publisher =
                    new HelloWorldFlow.HelloWorldPublisher.OfString(service, EXECUTOR);
            final var multi = Multi.createFrom().publisher(publisher);
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doWithMulti(multi);
        }
    }
}
