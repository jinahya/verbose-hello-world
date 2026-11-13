package com.github.jinahya.hello.api._java_sql;

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

import com.github.jinahya.hello.api.*;
import com.github.jinahya.hello.api.annotations.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateNClob(ResultSet, int)
 * updateNClob(resultSet, columnIndex)} method with real {@link ResultSet} implementations from JDBC
 * drivers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("JdbcHelloWorld.updateNClob(ResultSet, int)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class JdbcHelloWorld_UpdateNClob_ResultSet_Int__Test
        extends JdbcHelloWorld__Test {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var resultSet = invocation.getArgument(0, ResultSet.class);
            final var columnIndex = invocation.getArgument(1, Integer.class);
            resultSet.updateNClob(columnIndex, new StringReader(hello_world_string()));
            return resultSet;
        }).when(service()).updateNClob(
                ArgumentMatchers.<ResultSet>notNull(),
                ArgumentMatchers.intThat(v -> v >= 1)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:updatenclob_resultset_int_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "chars";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateNClob(ResultSet, int)
         * updateNClob(resultSet, columnIndex)} method updates a row with the {@code "hello, world"}
         * value through a real H2 updatable {@link ResultSet}.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INT PRIMARY KEY,
                                %s NCLOB
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
                        final var result = service().updateNClob(resultSet, 2);
                        assertSame(resultSet, result);
                        resultSet.updateRow();
                    }
                }
                {
                    final var sql = "SELECT %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        assertEquals(hello_world_string(), resultSet.getString(COLUMN));
                    }
                }
            }
        }
    }

    @DisplayName("HSQLDB")
    @Nested
    class Hsql_Test {

        private static final String URL = "jdbc:hsqldb:mem:updatenclob_resultset_int_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "chars";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateNClob(ResultSet, int)
         * updateNClob(resultSet, columnIndex)} method updates a row with the {@code "hello, world"}
         * value through a real HSQLDB updatable {@link ResultSet}.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INT PRIMARY KEY,
                                %s CLOB
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
                        final var result = service().updateNClob(resultSet, 2);
                        assertSame(resultSet, result);
                        resultSet.updateRow();
                    }
                }
                {
                    final var sql = "SELECT %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        assertEquals(hello_world_string(), resultSet.getString(COLUMN));
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

        private static final String COLUMN = "chars";

        /**
         * Verifies that the
         * {@link com.github.jinahya.hello.api.JdbcHelloWorld#updateNClob(ResultSet, int)
         * updateNClob(resultSet, columnIndex)} method updates a row with the {@code "hello, world"}
         * value through a real SQLite updatable {@link ResultSet}.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INTEGER PRIMARY KEY,
                                %s TEXT
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
                        final var result = service().updateNClob(resultSet, 2);
                        assertSame(resultSet, result);
                        resultSet.updateRow();
                    }
                }
                {
                    final var sql = "SELECT %s FROM %s".formatted(COLUMN, TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        assertTrue(resultSet.next());
                        assertEquals(hello_world_string(), resultSet.getString(COLUMN));
                    }
                }
            }
        }
    }
}
