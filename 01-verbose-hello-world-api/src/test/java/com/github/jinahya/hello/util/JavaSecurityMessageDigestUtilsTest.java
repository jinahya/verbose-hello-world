package com.github.jinahya.hello.util;

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class testing methods defined in {@link JavaSecurityMessageDigestUtils} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class JavaSecurityMessageDigestUtilsTest {

    @DisplayName("updateDigest(digest, buffer, bytes)")
    @Nested
    class UpdateDigestTest {

        @Test
        void __() throws NoSuchAlgorithmException {
            final var buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(128));
            buffer.limit(ThreadLocalRandom.current().nextInt(buffer.limit() + 1));
            buffer.position(
                    ThreadLocalRandom.current().nextInt(buffer.position(), buffer.remaining() + 1)
            );
            final var digest = MessageDigest.getInstance("SHA-1");
            final var bytes = ThreadLocalRandom.current().nextInt(0, buffer.position() + 1);
            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, bytes);
        }
    }
}
