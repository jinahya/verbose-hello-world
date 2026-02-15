package com.github.jinahya.hello.api._java_sql;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Set_Blob_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void _stub_set_array_will_set_hello_world_bytes_() {
        HelloWorldTestUtils.set_array_will_set_actual_hello_world_bytes(service());
    }

    @Test
    void _ThrowNullPointerException_ChecksumIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Blob blob = null;
        final var pos = 1L;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.set(blob, pos)
        );
    }

    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Blob blob = Mockito.mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.set(blob, pos)
        );
    }

    @Test
    void __()
            throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var blob = Mockito.mock(Blob.class);
        final var writtenList = new ArrayList<Integer>();
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE - HelloWorld.BYTES);
        // stub: setBytes returns random written (1 to len), simulating partial writes
        Mockito.when(blob.setBytes(
                        Mockito.longThat(v -> v >= pos),
                        Mockito.argThat(b -> b != null && b.length == HelloWorld.BYTES),
                        Mockito.intThat(v -> v >= 0),
                        Mockito.intThat(v -> v > 0)
                ))
                .thenAnswer(i -> {
                    final int len = i.getArgument(3, Integer.class);
                    final var sum = writtenList.stream().mapToInt(Integer::intValue).sum();
                    Assertions.assertEquals(HelloWorld.BYTES - sum, len);
                    final var written = ThreadLocalRandom.current().nextInt(1, len + 1);
                    writtenList.add(written);
                    return written;
                });
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(blob, pos);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        // capture all invocations of setBytes(pos, bytes, offset, len)
        final var posCaptor = ArgumentCaptor.forClass(Long.class);
        final var bytesCaptor = ArgumentCaptor.forClass(byte[].class);
        final var offsetCaptor = ArgumentCaptor.forClass(Integer.class);
        final var lenCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(blob, Mockito.atLeastOnce()).setBytes(                             // <2>
                posCaptor.capture(),
                bytesCaptor.capture(),
                offsetCaptor.capture(),
                lenCaptor.capture()
        );
        // verify: all invocations used the same array
        bytesCaptor.getAllValues().forEach(b -> Assertions.assertSame(array, b)); // <3>
        // verify: positions and offsets increase, lengths decrease by written values
        final var pValues = posCaptor.getAllValues();
        final var oValues = offsetCaptor.getAllValues();
        final var lValues = lenCaptor.getAllValues();
        for (int i = 0, j = 1; j < pValues.size(); i++, j++) {
            final var w = writtenList.get(i);
            Assertions.assertEquals(pValues.get(i) + w, pValues.get(j));
            Assertions.assertEquals(oValues.get(i) + w, oValues.get(j));
            Assertions.assertEquals(lValues.get(i) - w, lValues.get(j));
        }
        // verify: sum of written equals BYTES
        final var sum = writtenList.stream().mapToInt(Integer::intValue).sum();
        Assertions.assertEquals(HelloWorld.BYTES, sum);
        Assertions.assertSame(blob, result);                                             // <4>
    }

    @畵蛇添足
    @Test
    void __h2()
            throws SQLException {
        final var service = service();
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            {
                final var sql = "CREATE TABLE assets (id INT PRIMARY KEY, data BLOB)";
                conn.createStatement().execute(sql);
            }
            {
                final var sql = "INSERT INTO assets (id, data) VALUES (?, ?)";
                try (var ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, 1);
                    ps.setBytes(2, new byte[128]);
                    ps.executeUpdate();
                }
            }
            try (var rs = conn.createStatement().executeQuery("SELECT * FROM assets")) {
                Assertions.assertTrue(rs.next());
                final var blob = rs.getBlob(2);
                final var mutableBlob = new javax.sql.rowset.serial.SerialBlob(blob);
                final var pos = ThreadLocalRandom.current().nextLong(1L, 128L);
                final var result = service.set(mutableBlob, pos);
            }
        }
    }

    @Test
    void __hsql()
            throws SQLException {
        final var service = service();
        String url = "jdbc:hsqldb:mem:testdb";
        String user = "SA";
        String password = "";

        try (var connection = DriverManager.getConnection(url, user, password)) {
            {
                final var sql = "CREATE TABLE assets (id INT PRIMARY KEY, data BLOB)";
                connection.createStatement().execute(sql);
            }
            {
                final var sql = "INSERT INTO assets (id, data) VALUES (?, ?)";
                try (var ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, 1);
                    Blob blob = connection.createBlob();
                    blob.setBytes(1, new byte[128]);
                    ps.setBlob(2, blob);
                    ps.executeUpdate();
                }
            }
            try (var rs = connection.createStatement().executeQuery("SELECT * FROM assets")) {
                Assertions.assertTrue(rs.next());
                final var blob = rs.getBlob(2);
                final var mutableBlob = new javax.sql.rowset.serial.SerialBlob(blob);
                final var pos = ThreadLocalRandom.current().nextLong(1L, 128L);
                final var result = service.set(mutableBlob, pos);
            }
        }
    }

    @Disabled("java.sql.SQLFeatureNotSupportedException: not implemented by SQLite JDBC driver")
    @畵蛇添足
    @Test
    void __sqlite()
            throws SQLException {
        final var service = service();
        final var url = "jdbc:sqlite::memory:";
        try (var connection = DriverManager.getConnection(url)) {
            {
                final var sql = "CREATE TABLE assets (id INTEGER PRIMARY KEY, data BLOB)";
                connection.createStatement().execute(sql);
            }
            {
                final var sql = "INSERT INTO assets (id, data) VALUES (?, ?)";
                try (var ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, 1);

                    // SQLite Fix: Don't use connection.createBlob()
                    // Just send the bytes directly to the table
                    ps.setBytes(2, new byte[128]);
                    ps.executeUpdate();
                }
            }
            try (var rs = connection.createStatement().executeQuery("SELECT * FROM assets")) {
                Assertions.assertTrue(rs.next());
                final var blob = rs.getBlob(2);
                final var mutableBlob = new javax.sql.rowset.serial.SerialBlob(blob);
                final var pos = ThreadLocalRandom.current().nextLong(1L, 128L);
                final var result = service.set(mutableBlob, pos);
            }
        }
    }
}
