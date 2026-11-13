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

import java.io.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;

import static com.github.jinahya.hello.api.JdbcHelloWorldUtils.*;

/**
 * A package-private super interface of the {@link HelloWorld} interface for writing
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to various targets of the
 * {@code java.sql} package; prepared statements, callable statements, result sets, blobs, clobs,
 * and SQL output streams.
 * <p>
 * This interface exists only to keep the {@code java.sql} methods out of the {@link HelloWorld}
 * source file. The {@link HelloWorld} interface is its only sub-interface, and every method
 * declared here is a public method of the {@link HelloWorld} interface.
 * <p>
 * Every default method in this interface relies only on the {@link #set(byte[], int)} method, which
 * is the same method as the {@link HelloWorld#set(byte[], int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/package-summary.html">java.sql</a>
 */
interface JdbcHelloWorld {

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> on the specified
     * array starting at the specified index.
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array}.
     * @return the given {@code array}.
     * @see HelloWorld#set(byte[], int)
     */
    byte[] set(byte[] array, int index);

    // ------------------------------------------------------------------ java.sql.PreparedStatement

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an ASCII
     * stream value, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setAsciiStream(int, InputStream) setAsciiStream(parameterIndex,
     * stream)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * stream, {@link InputStream#close() closes} the stream, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[], int)
     * @see PreparedStatement#setAsciiStream(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setAsciiStream(final T preparedStatement,
                                                           final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        try (var x = new ByteArrayInputStream(array(this))) {
            preparedStatement.setAsciiStream(parameterIndex, x);
        }
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a binary
     * stream value, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setBinaryStream(int, InputStream) setBinaryStream(parameterIndex,
     * stream)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * stream, {@link InputStream#close() closes} the stream, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[], int)
     * @see PreparedStatement#setBinaryStream(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setBinaryStream(final T preparedStatement,
                                                            final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        try (var x = new ByteArrayInputStream(array(this))) {
            preparedStatement.setBinaryStream(parameterIndex, x);
        }
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the value of
     * the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link PreparedStatement#setBytes(int, byte[]) setBytes(parameterIndex, array)} method on the
     * {@code preparedStatement} with the {@code parameterIndex} and the array, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[], int)
     * @see PreparedStatement#setBytes(int, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setBytes(final T preparedStatement,
                                                     final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var array = array(this);
        preparedStatement.setBytes(parameterIndex, array);
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a string
     * value, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link PreparedStatement#setString(int, String) setString(parameterIndex, string)} method on
     * the {@code preparedStatement} with the {@code parameterIndex} and the string, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[], int)
     * @see PreparedStatement#setString(int, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setString(final T preparedStatement,
                                                      final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var string = string(this);
        preparedStatement.setString(parameterIndex, string);
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a character
     * stream value, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link PreparedStatement#setCharacterStream(int, Reader) setCharacterStream(parameterIndex,
     * reader)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * reader, {@link Reader#close() closes} the reader, and returns the {@code preparedStatement}.
     * @see #set(byte[], int)
     * @see PreparedStatement#setCharacterStream(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setCharacterStream(final T preparedStatement,
                                                               final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        try (var reader = new StringReader(string(this))) {
            preparedStatement.setCharacterStream(parameterIndex, reader);
        }
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character string value, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link PreparedStatement#setNString(int, String) setNString(parameterIndex, string)} method
     * on the {@code preparedStatement} with the {@code parameterIndex} and the string, and returns
     * the {@code preparedStatement}.
     * @see #set(byte[], int)
     * @see PreparedStatement#setNString(int, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setNString(final T preparedStatement,
                                                       final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var string = string(this);
        preparedStatement.setNString(parameterIndex, string);
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character stream value, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link PreparedStatement#setNCharacterStream(int, Reader) setNCharacterStream(parameterIndex,
     * reader)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * reader, and returns the {@code preparedStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see PreparedStatement#setNCharacterStream(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setNCharacterStream(final T preparedStatement,
                                                                final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var reader = new StringReader(string(this));
        preparedStatement.setNCharacterStream(parameterIndex, reader);
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code BLOB}
     * value read from an input stream, of the designated parameter on the specified prepared
     * statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setBlob(int, InputStream) setBlob(parameterIndex, stream)} method on
     * the {@code preparedStatement} with the {@code parameterIndex} and the stream, and returns the
     * {@code preparedStatement}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see PreparedStatement#setBlob(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setBlob(final T preparedStatement,
                                                    final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var stream = new ByteArrayInputStream(array(this));
        preparedStatement.setBlob(parameterIndex, stream);
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code CLOB}
     * value read from a reader, of the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link PreparedStatement#setClob(int, Reader) setClob(parameterIndex, reader)} method on the
     * {@code preparedStatement} with the {@code parameterIndex} and the reader, and returns the
     * {@code preparedStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see PreparedStatement#setClob(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setClob(final T preparedStatement,
                                                    final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var reader = new StringReader(string(this));
        preparedStatement.setClob(parameterIndex, reader);
        return preparedStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an
     * {@code NCLOB} value read from a reader, of the designated parameter on the specified prepared
     * statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link PreparedStatement#setNClob(int, Reader) setNClob(parameterIndex, reader)} method on
     * the {@code preparedStatement} with the {@code parameterIndex} and the reader, and returns the
     * {@code preparedStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see PreparedStatement#setNClob(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setNClob(final T preparedStatement,
                                                     final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var reader = new StringReader(string(this));
        preparedStatement.setNClob(parameterIndex, reader);
        return preparedStatement;
    }

    // ------------------------------------------------------------------ java.sql.CallableStatement

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an ASCII
     * stream value, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link CallableStatement#setAsciiStream(String, InputStream) setAsciiStream(parameterName,
     * stream)} method on the {@code callableStatement} with the {@code parameterName} and the
     * stream, and returns the {@code callableStatement}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setAsciiStream(String, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setAsciiStream(final T callableStatement,
                                                           final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var stream = new ByteArrayInputStream(array(this));
        callableStatement.setAsciiStream(parameterName, stream);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a binary
     * stream value, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link CallableStatement#setBinaryStream(String, InputStream) setBinaryStream(parameterName,
     * stream)} method on the {@code callableStatement} with the {@code parameterName} and the
     * stream, and returns the {@code callableStatement}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setBinaryStream(String, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setBinaryStream(final T callableStatement,
                                                            final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var stream = new ByteArrayInputStream(array(this));
        callableStatement.setBinaryStream(parameterName, stream);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an array of
     * bytes, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link CallableStatement#setBytes(String, byte[]) setBytes(parameterName, array)} method on
     * the {@code callableStatement} with the {@code parameterName} and the array, and returns the
     * {@code callableStatement}.
     * @see #set(byte[], int)
     * @see CallableStatement#setBytes(String, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setBytes(final T callableStatement,
                                                     final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var array = array(this);
        callableStatement.setBytes(parameterName, array);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a string
     * value, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link CallableStatement#setString(String, String) setString(parameterName, string)} method
     * on the {@code callableStatement} with the {@code parameterName} and the string, and returns
     * the {@code callableStatement}.
     * @see #set(byte[], int)
     * @see CallableStatement#setString(String, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setString(final T callableStatement,
                                                      final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var string = string(this);
        callableStatement.setString(parameterName, string);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a character
     * stream value, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link CallableStatement#setCharacterStream(String, Reader) setCharacterStream(parameterName,
     * reader)} method on the {@code callableStatement} with the {@code parameterName} and the
     * reader, and returns the {@code callableStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setCharacterStream(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setCharacterStream(final T callableStatement,
                                                               final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var reader = new StringReader(string(this));
        callableStatement.setCharacterStream(parameterName, reader);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character string value, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link CallableStatement#setNString(String, String) setNString(parameterName, string)} method
     * on the {@code callableStatement} with the {@code parameterName} and the string, and returns
     * the {@code callableStatement}.
     * @see #set(byte[], int)
     * @see CallableStatement#setNString(String, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setNString(final T callableStatement,
                                                       final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var string = string(this);
        callableStatement.setNString(parameterName, string);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character stream value, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link CallableStatement#setNCharacterStream(String, Reader)
     * setNCharacterStream(parameterName, reader)} method on the {@code callableStatement} with the
     * {@code parameterName} and the reader, and returns the {@code callableStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setNCharacterStream(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setNCharacterStream(final T callableStatement,
                                                                final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var reader = new StringReader(string(this));
        callableStatement.setNCharacterStream(parameterName, reader);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code BLOB}
     * value read from an input stream, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link CallableStatement#setBlob(String, InputStream) setBlob(parameterName, stream)} method
     * on the {@code callableStatement} with the {@code parameterName} and the stream, and returns
     * the {@code callableStatement}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setBlob(String, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setBlob(final T callableStatement,
                                                    final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var stream = new ByteArrayInputStream(array(this));
        callableStatement.setBlob(parameterName, stream);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code CLOB}
     * value read from a reader, of the named parameter on the specified callable statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link CallableStatement#setClob(String, Reader) setClob(parameterName, reader)} method on
     * the {@code callableStatement} with the {@code parameterName} and the reader, and returns the
     * {@code callableStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setClob(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setClob(final T callableStatement,
                                                    final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var reader = new StringReader(string(this));
        callableStatement.setClob(parameterName, reader);
        return callableStatement;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an
     * {@code NCLOB} value read from a reader, of the named parameter on the specified callable
     * statement.
     *
     * @param <T>               callable statement type parameter
     * @param callableStatement the callable statement on which the value is set.
     * @param parameterName     the name of the parameter.
     * @return the given {@code callableStatement}.
     * @throws NullPointerException if {@code callableStatement} or {@code parameterName} is
     *                              {@code null}.
     * @throws SQLException         if {@code parameterName} does not correspond to a named
     *                              parameter, if a database access error occurs, or if this method
     *                              is called on a closed {@link CallableStatement}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link CallableStatement#setNClob(String, Reader) setNClob(parameterName, reader)} method on
     * the {@code callableStatement} with the {@code parameterName} and the reader, and returns the
     * {@code callableStatement}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see CallableStatement#setNClob(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/CallableStatement.html">java.sql.CallableStatement</a>
     */
    default <T extends CallableStatement> T setNClob(final T callableStatement,
                                                     final String parameterName)
            throws SQLException {
        Objects.requireNonNull(callableStatement, "callableStatement is null");
        Objects.requireNonNull(parameterName, "parameterName is null");
        final var reader = new StringReader(string(this));
        callableStatement.setNClob(parameterName, reader);
        return callableStatement;
    }

    // -------------------------------------------------------------------------- java.sql.ResultSet

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an ASCII stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link ResultSet#updateAsciiStream(int, InputStream) updateAsciiStream(columnIndex, stream)}
     * method on the {@code resultSet} with the {@code columnIndex} and the stream, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateAsciiStream(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateAsciiStream(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var stream = new ByteArrayInputStream(array(this));
        resultSet.updateAsciiStream(columnIndex, stream);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a binary stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link ResultSet#updateBinaryStream(int, InputStream) updateBinaryStream(columnIndex,
     * stream)} method on the {@code resultSet} with the {@code columnIndex} and the stream, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateBinaryStream(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateBinaryStream(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var stream = new ByteArrayInputStream(array(this));
        resultSet.updateBinaryStream(columnIndex, stream);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an array of bytes.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link ResultSet#updateBytes(int, byte[]) updateBytes(columnIndex, array)} method on the
     * {@code resultSet} with the {@code columnIndex} and the array, and returns the
     * {@code resultSet}.
     * @apiNote This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateBytes(int, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateBytes(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var array = array(this);
        resultSet.updateBytes(columnIndex, array);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a string value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link ResultSet#updateString(int, String) updateString(columnIndex, string)} method on the
     * {@code resultSet} with the {@code columnIndex} and the string, and returns the
     * {@code resultSet}.
     * @apiNote This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateString(int, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateString(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var string = string(this);
        resultSet.updateString(columnIndex, string);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a character stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateCharacterStream(int, Reader) updateCharacterStream(columnIndex,
     * reader)} method on the {@code resultSet} with the {@code columnIndex} and the reader, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateCharacterStream(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateCharacterStream(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var reader = new StringReader(string(this));
        resultSet.updateCharacterStream(columnIndex, reader);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national character
     * string value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link ResultSet#updateNString(int, String) updateNString(columnIndex, string)} method on the
     * {@code resultSet} with the {@code columnIndex} and the string, and returns the
     * {@code resultSet}.
     * @apiNote This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateNString(int, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateNString(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var string = string(this);
        resultSet.updateNString(columnIndex, string);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national character
     * stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateNCharacterStream(int, Reader) updateNCharacterStream(columnIndex,
     * reader)} method on the {@code resultSet} with the {@code columnIndex} and the reader, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateNCharacterStream(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateNCharacterStream(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var reader = new StringReader(string(this));
        resultSet.updateNCharacterStream(columnIndex, reader);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code BLOB} value read
     * from an input stream.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link ResultSet#updateBlob(int, InputStream) updateBlob(columnIndex, stream)} method on the
     * {@code resultSet} with the {@code columnIndex} and the stream, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateBlob(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateBlob(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var stream = new ByteArrayInputStream(array(this));
        resultSet.updateBlob(columnIndex, stream);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code CLOB} value read
     * from a reader.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateClob(int, Reader) updateClob(columnIndex, reader)} method on the
     * {@code resultSet} with the {@code columnIndex} and the reader, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateClob(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateClob(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var reader = new StringReader(string(this));
        resultSet.updateClob(columnIndex, reader);
        return resultSet;
    }

    /**
     * Updates the designated column, of the current row of the specified result set, with the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an {@code NCLOB} value
     * read from a reader.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnIndex the first column is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code resultSet}.
     * @throws NullPointerException     if {@code resultSet} is {@code null}.
     * @throws IllegalArgumentException if {@code columnIndex} is not positive.
     * @throws SQLException             if {@code columnIndex} is not valid, if a database access
     *                                  error occurs, if the result set concurrency is
     *                                  {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if
     *                                  this method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateNClob(int, Reader) updateNClob(columnIndex, reader)} method on the
     * {@code resultSet} with the {@code columnIndex} and the reader, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateNClob(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateNClob(final T resultSet, final int columnIndex)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        if (columnIndex < 1) {
            throw new IllegalArgumentException("non-positive columnIndex: " + columnIndex);
        }
        final var reader = new StringReader(string(this));
        resultSet.updateNClob(columnIndex, reader);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an ASCII
     * stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link ResultSet#updateAsciiStream(String, InputStream) updateAsciiStream(columnLabel,
     * stream)} method on the {@code resultSet} with the {@code columnLabel} and the stream, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateAsciiStream(String, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateAsciiStream(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var stream = new ByteArrayInputStream(array(this));
        resultSet.updateAsciiStream(columnLabel, stream);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a binary
     * stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link ResultSet#updateBinaryStream(String, InputStream) updateBinaryStream(columnLabel,
     * stream)} method on the {@code resultSet} with the {@code columnLabel} and the stream, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateBinaryStream(String, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateBinaryStream(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var stream = new ByteArrayInputStream(array(this));
        resultSet.updateBinaryStream(columnLabel, stream);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an array of
     * bytes.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link ResultSet#updateBytes(String, byte[]) updateBytes(columnLabel, array)} method on the
     * {@code resultSet} with the {@code columnLabel} and the array, and returns the
     * {@code resultSet}.
     * @apiNote This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateBytes(String, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateBytes(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var array = array(this);
        resultSet.updateBytes(columnLabel, array);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a string
     * value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link ResultSet#updateString(String, String) updateString(columnLabel, string)} method on
     * the {@code resultSet} with the {@code columnLabel} and the string, and returns the
     * {@code resultSet}.
     * @apiNote This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateString(String, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateString(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var string = string(this);
        resultSet.updateString(columnLabel, string);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a character
     * stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateCharacterStream(String, Reader) updateCharacterStream(columnLabel,
     * reader)} method on the {@code resultSet} with the {@code columnLabel} and the reader, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateCharacterStream(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateCharacterStream(final T resultSet,
                                                          final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var reader = new StringReader(string(this));
        resultSet.updateCharacterStream(columnLabel, reader);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character string value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link ResultSet#updateNString(String, String) updateNString(columnLabel, string)} method on
     * the {@code resultSet} with the {@code columnLabel} and the string, and returns the
     * {@code resultSet}.
     * @apiNote This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateNString(String, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateNString(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var string = string(this);
        resultSet.updateNString(columnLabel, string);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character stream value.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateNCharacterStream(String, Reader) updateNCharacterStream(columnLabel,
     * reader)} method on the {@code resultSet} with the {@code columnLabel} and the reader, and
     * returns the {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateNCharacterStream(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateNCharacterStream(final T resultSet,
                                                           final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var reader = new StringReader(string(this));
        resultSet.updateNCharacterStream(columnLabel, reader);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code BLOB}
     * value read from an input stream.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link ResultSet#updateBlob(String, InputStream) updateBlob(columnLabel, stream)} method on
     * the {@code resultSet} with the {@code columnLabel} and the stream, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateBlob(String, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateBlob(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var stream = new ByteArrayInputStream(array(this));
        resultSet.updateBlob(columnLabel, stream);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a {@code CLOB}
     * value read from a reader.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateClob(String, Reader) updateClob(columnLabel, reader)} method on the
     * {@code resultSet} with the {@code columnLabel} and the reader, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateClob(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateClob(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var reader = new StringReader(string(this));
        resultSet.updateClob(columnLabel, reader);
        return resultSet;
    }

    /**
     * Updates the column with the specified label, of the current row of the specified result set,
     * with the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an
     * {@code NCLOB} value read from a reader.
     *
     * @param <T>         result set type parameter
     * @param resultSet   the result set whose current row is updated.
     * @param columnLabel the label for the column specified with the SQL {@code AS} clause; if the
     *                    {@code AS} clause was not specified, then the label is the name of the
     *                    column.
     * @return the given {@code resultSet}.
     * @throws NullPointerException if {@code resultSet} or {@code columnLabel} is {@code null}.
     * @throws SQLException         if {@code columnLabel} is not valid, if a database access error
     *                              occurs, if the result set concurrency is
     *                              {@link ResultSet#CONCUR_READ_ONLY CONCUR_READ_ONLY}, or if this
     *                              method is called on a closed result set.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link ResultSet#updateNClob(String, Reader) updateNClob(columnLabel, reader)} method on the
     * {@code resultSet} with the {@code columnLabel} and the reader, and returns the
     * {@code resultSet}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns. This method does not update the underlying database; the caller should invoke
     * {@link ResultSet#updateRow() updateRow()} or {@link ResultSet#insertRow() insertRow()} method
     * on the {@code resultSet}.
     * @see #set(byte[], int)
     * @see ResultSet#updateNClob(String, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/ResultSet.html">java.sql.ResultSet</a>
     */
    default <T extends ResultSet> T updateNClob(final T resultSet, final String columnLabel)
            throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet is null");
        Objects.requireNonNull(columnLabel, "columnLabel is null");
        final var reader = new StringReader(string(this));
        resultSet.updateNClob(columnLabel, reader);
        return resultSet;
    }

    // ------------------------------------------------------------------------------- java.sql.Blob

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * blob, through a binary stream obtained from the blob, starting at the specified position.
     *
     * @param <T>  blob type parameter
     * @param blob the blob to which the bytes are written.
     * @param pos  the position in the blob at which to start writing; the first byte is at position
     *             {@code 1}.
     * @return the given {@code blob}.
     * @throws NullPointerException     if {@code blob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if there is an error accessing the {@code BLOB} value.
     * @implSpec Default implementation opens an output stream by invoking
     * {@link Blob#setBinaryStream(long) blob.setBinaryStream(pos)} method on the {@code blob} with
     * the {@code pos}, writes, to the stream, an array of the hello-world-bytes set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, {@link OutputStream#flush() flushes}
     * and {@link OutputStream#close() closes} the stream, and returns the {@code blob}.
     * @see #set(byte[], int)
     * @see Blob#setBinaryStream(long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/Blob.html">java.sql.Blob</a>
     */
    default <T extends Blob> T setBinaryStream(final T blob, final long pos)
            throws SQLException, IOException {
        Objects.requireNonNull(blob, "blob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        try (var stream = blob.setBinaryStream(pos)) {
            stream.write(array(this));
            stream.flush();
        }
        return blob;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> in the specified
     * blob, starting at the specified position.
     *
     * @param <T>  blob type parameter
     * @param blob the blob on which the bytes are set.
     * @param pos  the position in the blob at which to start writing; the first byte is at position
     *             {@code 1}.
     * @return the given {@code blob}.
     * @throws NullPointerException     if {@code blob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws SQLException             if there is an error accessing the {@code BLOB} value.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link Blob#setBytes(long, byte[]) blob.setBytes(pos, array)} method on the {@code blob} with
     * the {@code pos} and the array, and returns the {@code blob}.
     * @see #set(byte[], int)
     * @see Blob#setBytes(long, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/Blob.html">java.sql.Blob</a>
     */
    default <T extends Blob> T setBytes(final T blob, final long pos) throws SQLException {
        Objects.requireNonNull(blob, "blob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var array = array(this);
        blob.setBytes(pos, array);
        return blob;
    }

    // ------------------------------------------------------------------------------- java.sql.Clob

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * clob, through an ASCII stream obtained from the clob, starting at the specified position.
     *
     * @param <T>  clob type parameter
     * @param clob the clob to which the bytes are written.
     * @param pos  the position in the clob at which to start writing; the first character is at
     *             position {@code 1}.
     * @return the given {@code clob}.
     * @throws NullPointerException     if {@code clob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if there is an error accessing the {@code CLOB} value.
     * @implSpec Default implementation opens an output stream by invoking
     * {@link Clob#setAsciiStream(long) clob.setAsciiStream(pos)} method on the {@code clob} with
     * the {@code pos}, writes, to the stream, an array of the hello-world-bytes set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, {@link OutputStream#flush() flushes}
     * and {@link OutputStream#close() closes} the stream, and returns the {@code clob}.
     * @see #set(byte[], int)
     * @see Clob#setAsciiStream(long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
     */
    default <T extends Clob> T setAsciiStream(final T clob, final long pos)
            throws SQLException, IOException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        try (var stream = clob.setAsciiStream(pos)) {
            stream.write(array(this));
            stream.flush();
        }
        return clob;
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * clob, through a character stream obtained from the clob, starting at the specified position.
     *
     * @param <T>  clob type parameter
     * @param clob the clob to which the characters are written.
     * @param pos  the position in the clob at which to start writing; the first character is at
     *             position {@code 1}.
     * @return the given {@code clob}.
     * @throws NullPointerException     if {@code clob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if there is an error accessing the {@code CLOB} value.
     * @implSpec Default implementation opens a writer by invoking
     * {@link Clob#setCharacterStream(long) clob.setCharacterStream(pos)} method on the {@code clob}
     * with the {@code pos}, writes, to the writer, a string of the hello-world-bytes decoded, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, from the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, {@link Writer#flush() flushes} and
     * {@link Writer#close() closes} the writer, and returns the {@code clob}.
     * @see #set(byte[], int)
     * @see Clob#setCharacterStream(long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
     */
    default <T extends Clob> T setCharacterStream(final T clob, final long pos)
            throws SQLException, IOException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        try (var writer = clob.setCharacterStream(pos)) {
            writer.write(string(this));
            writer.flush();
        }
        return clob;
    }

    /**
     * Sets the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a string, in
     * the specified clob starting at the specified position.
     *
     * @param <T>  clob type parameter
     * @param clob the clob into which the string is written.
     * @param pos  the position at which writing starts; the first character is at position
     *             {@code 1}.
     * @return the given {@code clob}.
     * @throws NullPointerException     if {@code clob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws SQLException             if there is an error accessing the {@code CLOB} value, or if
     *                                  {@code pos} is greater than the length of the {@code clob}.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link Clob#setString(long, String) clob.setString(pos, string)} method on the {@code clob}
     * with the {@code pos} and the string, and returns the {@code clob}.
     * @see #set(byte[], int)
     * @see Clob#setString(long, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
     */
    default <T extends Clob> T setString(final T clob, final long pos) throws SQLException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var string = string(this);
        clob.setString(pos, string);
        return clob;
    }

    // -------------------------------------------------------------------------- java.sql.SQLOutput

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an ASCII
     * stream, to the specified SQL output stream.
     *
     * @param <T>    SQL output type parameter
     * @param output the SQL output stream to which the value is written.
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws SQLException         if a database access error occurs.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link SQLOutput#writeAsciiStream(InputStream) writeAsciiStream(stream)} method on the
     * {@code output} with the stream, and returns the {@code output}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see SQLOutput#writeAsciiStream(InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
     */
    default <T extends SQLOutput> T writeAsciiStream(final T output) throws SQLException {
        Objects.requireNonNull(output, "output is null");
        final var stream = new ByteArrayInputStream(array(this));
        output.writeAsciiStream(stream);
        return output;
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a binary
     * stream, to the specified SQL output stream.
     *
     * @param <T>    SQL output type parameter
     * @param output the SQL output stream to which the value is written.
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws SQLException         if a database access error occurs.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the array in a
     * {@link ByteArrayInputStream}, invokes
     * {@link SQLOutput#writeBinaryStream(InputStream) writeBinaryStream(stream)} method on the
     * {@code output} with the stream, and returns the {@code output}.
     * @apiNote This method does not close the stream, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see SQLOutput#writeBinaryStream(InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
     */
    default <T extends SQLOutput> T writeBinaryStream(final T output) throws SQLException {
        Objects.requireNonNull(output, "output is null");
        final var stream = new ByteArrayInputStream(array(this));
        output.writeBinaryStream(stream);
        return output;
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as an array of
     * bytes, to the specified SQL output stream.
     *
     * @param <T>    SQL output type parameter
     * @param output the SQL output stream to which the value is written.
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws SQLException         if a database access error occurs.
     * @implSpec Default implementation obtains an array of the hello-world-bytes by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link SQLOutput#writeBytes(byte[]) writeBytes(array)} method on the {@code output} with the
     * array, and returns the {@code output}.
     * @see #set(byte[], int)
     * @see SQLOutput#writeBytes(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
     */
    default <T extends SQLOutput> T writeBytes(final T output) throws SQLException {
        Objects.requireNonNull(output, "output is null");
        final var array = array(this);
        output.writeBytes(array);
        return output;
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a string, to
     * the specified SQL output stream.
     *
     * @param <T>    SQL output type parameter
     * @param output the SQL output stream to which the value is written.
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws SQLException         if a database access error occurs.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link SQLOutput#writeString(String) writeString(string)} method on the {@code output} with
     * the string, and returns the {@code output}.
     * @see #set(byte[], int)
     * @see SQLOutput#writeString(String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
     */
    default <T extends SQLOutput> T writeString(final T output) throws SQLException {
        Objects.requireNonNull(output, "output is null");
        final var string = string(this);
        output.writeString(string);
        return output;
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a character
     * stream, to the specified SQL output stream.
     *
     * @param <T>    SQL output type parameter
     * @param output the SQL output stream to which the value is written.
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws SQLException         if a database access error occurs.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, wraps the string in a
     * {@link StringReader}, invokes
     * {@link SQLOutput#writeCharacterStream(Reader) writeCharacterStream(reader)} method on the
     * {@code output} with the reader, and returns the {@code output}.
     * @apiNote This method does not close the reader, since the driver may still read it after this
     * method returns.
     * @see #set(byte[], int)
     * @see SQLOutput#writeCharacterStream(Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
     */
    default <T extends SQLOutput> T writeCharacterStream(final T output) throws SQLException {
        Objects.requireNonNull(output, "output is null");
        final var reader = new StringReader(string(this));
        output.writeCharacterStream(reader);
        return output;
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, as a national
     * character string, to the specified SQL output stream.
     *
     * @param <T>    SQL output type parameter
     * @param output the SQL output stream to which the value is written.
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws SQLException         if a database access error occurs.
     * @implSpec Default implementation obtains a string of the hello-world-bytes by decoding, in
     * {@link StandardCharsets#US_ASCII US-ASCII}, the array set by invoking
     * {@link #set(byte[], int) set(new byte[12], 0)} method, invokes
     * {@link SQLOutput#writeNString(String) writeNString(string)} method on the {@code output} with
     * the string, and returns the {@code output}.
     * @see #set(byte[], int)
     * @see SQLOutput#writeNString(String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.sql/java/sql/SQLOutput.html">java.sql.SQLOutput</a>
     */
    default <T extends SQLOutput> T writeNString(final T output) throws SQLException {
        Objects.requireNonNull(output, "output is null");
        final var string = string(this);
        output.writeNString(string);
        return output;
    }
}
