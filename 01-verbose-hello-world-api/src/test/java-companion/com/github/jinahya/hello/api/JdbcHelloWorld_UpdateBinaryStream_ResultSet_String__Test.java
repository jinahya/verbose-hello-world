package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.annotations.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.JdbcHelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring
 * {@link JdbcHelloWorld#updateBinaryStream(ResultSet, String)
 * updateBinaryStream(resultSet, columnLabel)} method with real {@link ResultSet} implementations
 * from JDBC drivers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("JdbcHelloWorld.updateBinaryStream(ResultSet, String)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class JdbcHelloWorld_UpdateBinaryStream_ResultSet_String__Test
        extends JdbcHelloWorld__Test {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var resultSet = invocation.getArgument(0, ResultSet.class);
            final var columnLabel = invocation.getArgument(1, String.class);
            resultSet.updateBinaryStream(
                    columnLabel,
                    new ByteArrayInputStream(hello_world_byte_array())
            );
            return resultSet;
        }).when(service()).updateBinaryStream(
                ArgumentMatchers.<ResultSet>notNull(),
                ArgumentMatchers.notNull()
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:updatebinarystream_resultset_string_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link JdbcHelloWorld#updateBinaryStream(ResultSet, String)
         * updateBinaryStream(resultSet, columnLabel)} method updates a row with the
         * {@code "hello, world"} value through a real H2 updatable {@link ResultSet}.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INT PRIMARY KEY,
                                %s VARBINARY(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (id) VALUES (1)".formatted(TABLE);
                    try (var statement = connection.createStatement()) {
                        assertEquals(1, statement.executeUpdate(sql));
                    }
                }
                {
                    final var sql = "SELECT id, %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement(
                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        final var result = service().updateBinaryStream(resultSet, COLUMN);
                        assertSame(resultSet, result);
                        resultSet.updateRow();
                    }
                }
                {
                    final var sql = "SELECT %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        assertArrayEquals(hello_world_byte_array(), resultSet.getBytes(COLUMN));
                    }
                }
            }
        }
    }

    @DisplayName("SQLite")
    @Disabled("SQLite JDBC driver only supports CONCUR_READ_ONLY result sets")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link JdbcHelloWorld#updateBinaryStream(ResultSet, String)
         * updateBinaryStream(resultSet, columnLabel)} method updates a row with the
         * {@code "hello, world"} value through a real SQLite updatable {@link ResultSet}.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INTEGER PRIMARY KEY,
                                %s BLOB
                            )"""
                            .formatted(TABLE, COLUMN);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (id) VALUES (1)".formatted(TABLE);
                    try (var statement = connection.createStatement()) {
                        assertEquals(1, statement.executeUpdate(sql));
                    }
                }
                {
                    final var sql = "SELECT id, %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement(
                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        final var result = service().updateBinaryStream(resultSet, COLUMN);
                        assertSame(resultSet, result);
                        resultSet.updateRow();
                    }
                }
                {
                    final var sql = "SELECT %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        assertArrayEquals(hello_world_byte_array(), resultSet.getBytes(COLUMN));
                    }
                }
            }
        }
    }
}
