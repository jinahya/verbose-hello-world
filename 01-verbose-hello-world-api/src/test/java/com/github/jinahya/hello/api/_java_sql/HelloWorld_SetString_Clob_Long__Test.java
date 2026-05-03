package com.github.jinahya.hello.api._java_sql;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestConstants;
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

import java.sql.Clob;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetString_Clob_Long__Test
        extends HelloWorldTest {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var c = invocation.getArgument(0, Clob.class);
            final var p = invocation.getArgument(1, Long.class);
            c.setString(p, HelloWorldTestConstants.HELLO_WORLD_STRING);
            return c;
        }).when(service()).setString(
                ArgumentMatchers.<Clob>notNull(),
                ArgumentMatchers.longThat(v -> v >= 1L)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:setstring_clob_long_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                Assertions.assertEquals(0L, clob.length());
                final var pos = 1L;
                final var result = service().setString(clob, pos);
                Assertions.assertSame(clob, result);
                Assertions.assertEquals(HelloWorld.BYTES, clob.length());
                Assertions.assertEquals(HelloWorldTestConstants.HELLO_WORLD_STRING,
                                        clob.getSubString(1L, HelloWorld.BYTES));
            }
        }
    }

    @Nested
    class Hsql_Test {

        private static final String URL = "jdbc:hsqldb:mem:setstring_clob_long_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var pos = 1L;
                final var result = service().setString(clob, pos);
                Assertions.assertSame(clob, result);
                Assertions.assertEquals(HelloWorld.BYTES, clob.length());
                Assertions.assertEquals("hello, world", clob.getSubString(1L, HelloWorld.BYTES));
            }
        }
    }

    @Disabled("SQLite JDBC driver does not implement Connection.createClob()")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                final var clob = connection.createClob();
                final var pos = 1L;
                final var result = service().setString(clob, pos);
                Assertions.assertSame(clob, result);
                Assertions.assertEquals(HelloWorld.BYTES, clob.length());
                Assertions.assertEquals("hello, world", clob.getSubString(1L, HelloWorld.BYTES));
            }
        }
    }
}
