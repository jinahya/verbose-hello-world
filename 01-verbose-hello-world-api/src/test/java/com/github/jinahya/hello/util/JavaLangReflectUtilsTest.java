package com.github.jinahya.hello.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaLangReflectUtilsTest {

    @Nested
    class UncloseableProxyTest {

        @Test
        void __() {
            final var closeable = Mockito.mock(AutoCloseable.class);
            final var proxy = JavaLangReflectUtils.uncloseableProxy(closeable);
            assertThatThrownBy(proxy::close)
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(proxy::close)
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class NonIdempotentCloseableProxyTest {

        @Test
        void __() {
            final var closeable = Mockito.mock(AutoCloseable.class);
            final var proxy = JavaLangReflectUtils.nonIdempotentCloseableProxy(closeable);
            assertThatCode(proxy::close)
                    .doesNotThrowAnyException();
            assertThatThrownBy(proxy::close)
                    .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(proxy::close)
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
