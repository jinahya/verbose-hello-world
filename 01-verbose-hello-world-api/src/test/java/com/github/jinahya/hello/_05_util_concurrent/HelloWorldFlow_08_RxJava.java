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

import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class HelloWorldFlow_08_RxJava extends _HelloWorldTest {

    @Test
    void __() {
//        final var subscriber = new org.reactivestreams.Subscriber<Byte>() { // @formatter:off
//            @Override public void onSubscribe(final org.reactivestreams.Subscription s) {
//                log.debug("onSubscribe({})", s);
//                assert s != null : "subscription supposed to be non-null";
//                s.request(HelloWorld.BYTES);
//            }
//            @Override public void onNext(final Byte t) {
//                log.debug("onNext({})", t);
//            }
//            @Override public void onError(final Throwable t) {
//                log.error("onError({})", t, t);
//            }
//            @Override public void onComplete() {
//                log.debug("onComplete()");
//            }
//        }; // @formatter:on
//        final var executor = Executors.newSingleThreadExecutor(
//                Thread.ofVirtual().name("byte-publisher-", 0L).factory()
//        );
//        HelloWorldFlow.newPublisherForBytes(service(), executor)
//                .subscribe(FlowAdapters.toFlowSubscriber(subscriber));
    }
}
