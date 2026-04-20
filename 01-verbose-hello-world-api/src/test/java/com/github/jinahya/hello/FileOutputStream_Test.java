package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@ExtendWith({MockitoExtension.class})
@Slf4j
class FileOutputStream_Test {

    @Test
    void __NotExist(final @TempDir File dir) throws IOException {
        final var file = new File(dir, "test.txt");
        Assertions.assertFalse(file.exists());
        new FileOutputStream(file).close();
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(0L, file.length());
    }

    @Test
    void __NotExistAppend(final @TempDir File dir) throws IOException {
        final var file = new File(dir, "test.txt");
        Assertions.assertFalse(file.exists());
        new FileOutputStream(file, true).close();
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(0L, file.length());
    }

    @Test
    void __ExistNotAppend(final @TempDir File dir) throws IOException {
        final var file = new File(dir, "test.txt");
        try (var stream = new FileOutputStream(file)) {
            stream.write(0);
            stream.flush();
        }
        Assertions.assertTrue(file.exists());
        new FileOutputStream(file).close();
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(0L, file.length());
    }
}
