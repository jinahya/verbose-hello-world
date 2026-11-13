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

import java.sql.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.JdbcHelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring
 * {@link JdbcHelloWorld#setBytes(CallableStatement, String)
 * setBytes(callableStatement, parameterName)} method with real {@link CallableStatement}
 * implementations from JDBC drivers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("JdbcHelloWorld.setBytes(CallableStatement, String)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class JdbcHelloWorld_SetBytes_CallableStatement_String__Test
        extends JdbcHelloWorld__Test {

    @BeforeEach
    void __() throws SQLException {
        Mockito.doAnswer(invocation -> {
            final var callableStatement = invocation.getArgument(0, CallableStatement.class);
            final var parameterName = invocation.getArgument(1, String.class);
            callableStatement.setBytes(parameterName, hello_world_byte_array());
            return callableStatement;
        }).when(service()).setBytes(
                ArgumentMatchers.<CallableStatement>notNull(),
                ArgumentMatchers.notNull()
        );
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("H2")
    @Disabled("H2 JDBC driver does not resolve parameter names of a CallableStatement")
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:setbytes_callablestatement_string_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link JdbcHelloWorld#setBytes(CallableStatement, String)
         * setBytes(callableStatement, parameterName)} method passes the {@code "hello, world"}
         * value, through a real H2 {@link CallableStatement}, to a stored procedure which inserts
         * it.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                %s VARBINARY(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = """
                            CREATE PROCEDURE hello_world_insert(IN v VARBINARY(%d))
                            MODIFIES SQL DATA
                            INSERT INTO %s (%s) VALUES (v)"""
                            .formatted(HelloWorld.BYTES, TABLE, COLUMN);
                    try (var statement = connection.createStatement()) {
                        statement.executeUpdate(sql);
                    }
                }
                {
                    try (var statement = connection.prepareCall("{call hello_world_insert(?)}")) {
                        final var result = service().setBytes(statement, "V");
                        assertSame(statement, result);
                        statement.execute();
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
    @Disabled("SQLite JDBC driver does not support stored procedures")
    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "bytes";

        /**
         * Verifies that the
         * {@link JdbcHelloWorld#setBytes(CallableStatement, String)
         * setBytes(callableStatement, parameterName)} method passes the {@code "hello, world"}
         * value, through a real SQLite {@link CallableStatement}, to a stored procedure which
         * inserts it.
         */
        @DisplayName("happy path")
        @Test
        void __() throws SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                %s BLOB
                            )"""
                            .formatted(TABLE, COLUMN);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = """
                            CREATE PROCEDURE hello_world_insert(IN v BLOB)
                            MODIFIES SQL DATA
                            INSERT INTO %s (%s) VALUES (v)"""
                            .formatted(TABLE, COLUMN);
                    try (var statement = connection.createStatement()) {
                        statement.executeUpdate(sql);
                    }
                }
                {
                    try (var statement = connection.prepareCall("{call hello_world_insert(?)}")) {
                        final var result = service().setBytes(statement, "V");
                        assertSame(statement, result);
                        statement.execute();
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
