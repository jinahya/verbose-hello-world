package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A class for testing {@link HelloWorld#put(ZipOutputStream, String) put(stream, name)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(stream, name)")
@Slf4j
class HelloWorld_Put_ZipOutputStream_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#put(ZipOutputStream, String) put(stream, name)} method
     * throws a {@link NullPointerException} when the {@code stream} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <stream> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_StreamIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = (ZipOutputStream) null;
        final var name = "entry";
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(stream, name)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#put(ZipOutputStream, String) put(stream, name)} method
     * throws a {@link NullPointerException} when the {@code name} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <name> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_NameIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = Mockito.mock(ZipOutputStream.class);
        final var name = (String) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(stream, name)
        );
    }

    /**
     * Asserts {@link HelloWorld#put(ZipOutputStream, String) put(stream, name)} method invokes
     * {@link ZipOutputStream#putNextEntry(ZipEntry) putNextEntry(entry)},
     * {@link HelloWorld#write(OutputStream) write(stream)}, and
     * {@link ZipOutputStream#closeEntry() closeEntry()} on the {@code stream}, and returns the
     * {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <putNextEntry>, <write(stream)>, and <closeEntry>
            and return the <stream>"""
    )
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var stream = Mockito.mock(ZipOutputStream.class);
        final var name = "hello.txt";
        try (var construction = Mockito.mockConstruction(ZipEntry.class, (_, c) -> {
            Assertions.assertEquals(1, c.arguments().size());
            Assertions.assertEquals(name, c.arguments().getFirst());
        })) {
            // -------------------------------------------------------------------------------- when
            final var result = service.put(stream, name);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, construction.constructed().size());
            final var entry = construction.constructed().getFirst();
            Mockito.verify(stream, Mockito.times(1)).putNextEntry(entry);
            Mockito.verify(stream, Mockito.times(1)).closeEntry();
            Assertions.assertSame(stream, result);
        }
    }

    /**
     * Asserts {@link HelloWorld#put(ZipOutputStream, String) put(stream, name)} method writes the
     * {@code hello, world} bytes as a zip entry to a {@link ByteArrayOutputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @畵蛇添足("testing with a real ZipOutputStream backed by ByteArrayOutputStream")
    @DisplayName("should write a zip entry to a <ByteArrayOutputStream>")
    @Test
    void _添足_畵蛇()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var s = i.getArgument(0, OutputStream.class);
            s.write(HelloWorldTestUtils.hello_world_byte_array());
            return s;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        final var baos = new ByteArrayOutputStream();
        final var name = "hello.txt";
        // ------------------------------------------------------------------------------------ when
        try (var stream = new ZipOutputStream(baos)) {
            final var result = service.put(stream, name);
            assert result == stream;
            stream.flush(); // maybe redundant, not harmful
        }
        // ------------------------------------------------------------------------------------ then
        log.debug("length: {}", baos.size());
    }

    /**
     * Asserts {@link HelloWorld#put(ZipOutputStream, String) put(stream, name)} method writes the
     * {@code hello, world} bytes as a zip entry to a {@link FileOutputStream}.
     *
     * @param dir the temporary directory.
     * @throws IOException if an I/O error occurs.
     */
    @畵蛇添足("testing with a real ZipOutputStream backed by FileOutputStream")
    @DisplayName("should write a zip entry to a <FileOutputStream>")
    @Test
    void _添足_畵蛇(@TempDir final File dir)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
            final var s = i.getArgument(0, OutputStream.class);
            s.write(HelloWorldTestUtils.hello_world_byte_array());
            return s;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        final var file = File.createTempFile("tmp", ".zip", dir);
        final var name = "hello.txt";
        // ------------------------------------------------------------------------------------ when
        try (var stream = new ZipOutputStream(new FileOutputStream(file))) {
            final var result = service.put(stream, name);
            assert result == stream;
            stream.flush(); // maybe redundant, not harmful
        }
        // ------------------------------------------------------------------------------------ then
        Assertions.assertTrue(file.length() > 0L);
        log.debug("length: {}", file.length());
    }
}
