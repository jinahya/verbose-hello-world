package com.github.jinahya.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A class for testing {@link IHelloWorldServerUtils} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class IHelloWorldServerUtilsTest {

    /**
     * Asserts {@link IHelloWorldServerUtils#loadHelloWorld()} returns non-null instance.
     */
    @DisplayName("load() return non-null")
    @Test
    void load_NotNull_() {
        final HelloWorld helloWorld = IHelloWorldServerUtils.loadHelloWorld();
        Assertions.assertNotNull(helloWorld);
    }
}