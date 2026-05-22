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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetAsciiStream_PreparedStatement_Int__Test
        extends HelloWorldTest {

    @BeforeEach
    void __() throws IOException, SQLException {
        Mockito.doAnswer(invocation -> {
            final var ps = invocation.getArgument(0, PreparedStatement.class);
            final var pi = invocation.getArgument(1, Integer.class);
            ps.setAsciiStream(
                    pi,
                    new ByteArrayInputStream(HelloWorldTestUtils.hello_world_byte_array())
            );
            return ps;
        }).when(service()).setAsciiStream(
                ArgumentMatchers.<PreparedStatement>notNull(),
                ArgumentMatchers.intThat(v -> v >= 1)
        );
        HelloWorldTestUtils.set_array_returns_the_array(service());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class H2_Test {

        private static final String URL
                = "jdbc:h2:mem:setasciistream_preparedstatement_int_testdb;DB_CLOSE_DELAY=-1";

        private static final String USER = "sa";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "chars";

        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                %s VARCHAR(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (%s) VALUES (?)".formatted(TABLE, COLUMN);
                    try (var statement = connection.prepareStatement(sql)) {
                        service().setAsciiStream(statement, 1);
                        final var result = statement.executeUpdate();
                        assert result == 1;
                    }
                }
                {
                    final var sql = "SELECT * FROM %s".formatted(TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        Assertions.assertTrue(resultSet.next());
                        try (var stream = resultSet.getAsciiStream(COLUMN)) {
                            Assertions.assertNotNull(stream);
                            final var bytes = stream.readAllBytes();
                            Assertions.assertEquals(HelloWorld.BYTES, bytes.length);
                            Assertions.assertArrayEquals(
                                    HelloWorldTestUtils.hello_world_byte_array(),
                                    bytes
                            );
                        }
                    }
                }
            }
        }
    }

    @Nested
    class Hsql_Test {

        private static final String URL
                = "jdbc:hsqldb:mem:setasciistream_preparedstatement_int_testdb";

        private static final String USER = "SA";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "chars";

        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                %s VARCHAR(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (%s) VALUES (?)".formatted(TABLE, COLUMN);
                    try (var statement = connection.prepareStatement(sql)) {
                        service().setAsciiStream(statement, 1);
                        final var result = statement.executeUpdate();
                        assert result == 1;
                    }
                }
                {
                    final var sql = "SELECT * FROM %s".formatted(TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        Assertions.assertTrue(resultSet.next());
                        try (var stream = resultSet.getAsciiStream(COLUMN)) {
                            Assertions.assertNotNull(stream);
                            final var bytes = stream.readAllBytes();
                            Assertions.assertEquals(HelloWorld.BYTES, bytes.length);
                            Assertions.assertArrayEquals(
                                    HelloWorldTestUtils.hello_world_byte_array(),
                                    bytes
                            );
                        }
                    }
                }
            }
        }
    }

    @Nested
    class SQLite_Test {

        private static final String URL = "jdbc:sqlite::memory:";

        private static final String USER = "";

        private static final String PASSWORD = "";

        private static final String TABLE = "hello_world";

        private static final String COLUMN = "chars";

        @Test
        void __() throws IOException, SQLException {
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                {
                    final var sql = """
                            CREATE TABLE %s (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                %s VARCHAR(%d)
                            )"""
                            .formatted(TABLE, COLUMN, HelloWorld.BYTES);
                    try (var statement = connection.createStatement()) {
                        final var result = statement.executeUpdate(sql);
                        assert result == 0;
                    }
                }
                {
                    final var sql = "INSERT INTO %s (%s) VALUES (?)".formatted(TABLE, COLUMN);
                    try (var statement = connection.prepareStatement(sql)) {
                        service().setAsciiStream(statement, 1);
                        final var result = statement.executeUpdate();
                        assert result == 1;
                    }
                }
                {
                    final var sql = "SELECT * FROM %s".formatted(TABLE);
                    try (var statement = connection.createStatement();
                         var resultSet = statement.executeQuery(sql)) {
                        Assertions.assertTrue(resultSet.next());
                        try (var stream = resultSet.getAsciiStream(COLUMN)) {
                            Assertions.assertNotNull(stream);
                            final var bytes = stream.readAllBytes();
                            Assertions.assertEquals(HelloWorld.BYTES, bytes.length);
                            Assertions.assertArrayEquals(
                                    HelloWorldTestUtils.hello_world_byte_array(),
                                    bytes
                            );
                        }
                    }
                }
            }
        }
    }
}
