package com.github.jinahya.hello.api._java_sql;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.sql.Blob;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetBytes_Blob_Long__Test
        extends HelloWorldTest {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var blob = invocation.getArgument(0, Blob.class);
            final var pos = invocation.getArgument(1, Long.class);
            blob.setBytes(pos, HelloWorldTestUtils.hello_world_byte_array());
            return blob;
        }).when(service()).setBytes(
                ArgumentMatchers.<Blob>notNull(),
                ArgumentMatchers.longThat(v -> v >= 1L)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class H2_Test {

        private static final String URL = "jdbc:h2:mem:setbytes_blob_long_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var blob = connection.createBlob();
                final var result = service().setBytes(blob, 1L);
                Assertions.assertSame(blob, result);
                Assertions.assertEquals(HelloWorld.BYTES, blob.length());
                Assertions.assertArrayEquals(
                        HelloWorldTestUtils.hello_world_byte_array(),
                        blob.getBytes(1L, HelloWorld.BYTES)
                );
            }
        }
    }

    @Nested
    class Hsql_Test {

        private static final String URL = "jdbc:hsqldb:mem:setbytes_blob_long_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var blob = connection.createBlob();
                final var result = service().setBytes(blob, 1L);
                Assertions.assertSame(blob, result);
                Assertions.assertEquals(HelloWorld.BYTES, blob.length());
                Assertions.assertArrayEquals(
                        HelloWorldTestUtils.hello_world_byte_array(),
                        blob.getBytes(1L, HelloWorld.BYTES)
                );
            }
        }
    }

    @Disabled("SQLite JDBC driver does not implement Connection.createBlob()")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var blob = connection.createBlob();
                final var result = service().setBytes(blob, 1L);
                Assertions.assertSame(blob, result);
            }
        }
    }
}
