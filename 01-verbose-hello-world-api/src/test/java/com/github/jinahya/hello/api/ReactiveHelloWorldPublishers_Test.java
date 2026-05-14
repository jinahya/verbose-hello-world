package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Factory-level smoke tests for {@link ReactiveHelloWorldPublishers}.
 * <p>
 * Each test verifies that the corresponding factory method returns a non-{@code null} publisher
 * without throwing — wiring-level smoke only. Subscription-level behaviour (demand, completion,
 * Rule 3.9 / 3.12, …) is verified on the publisher classes directly by their per-publisher tests.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("ReactiveHelloWorldPublishers — factory smoke")
@ExtendWith({MockitoExtension.class})
@Slf4j
class ReactiveHelloWorldPublishers_Test {

    @Mock
    private HelloWorld service;

    @Test
    @DisplayName("ofBytes(service) returns a non-null Publisher<Byte>")
    void ofBytes__() {
        Assertions.assertNotNull(ReactiveHelloWorldPublishers.ofBytes(service));
    }

    @Test
    @DisplayName("ofArrays(service) returns a non-null Publisher<byte[]>")
    void ofArrays__() {
        Assertions.assertNotNull(ReactiveHelloWorldPublishers.ofArrays(service));
    }

    @Test
    @DisplayName("ofStrings(service) returns a non-null Publisher<String>")
    void ofStrings__() {
        Assertions.assertNotNull(ReactiveHelloWorldPublishers.ofStrings(service));
    }
}
