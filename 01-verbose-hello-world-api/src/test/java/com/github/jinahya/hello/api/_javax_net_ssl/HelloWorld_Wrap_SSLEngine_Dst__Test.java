package com.github.jinahya.hello.api._javax_net_ssl;


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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import javax.net.ssl.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
 * method with a real pair of {@link SSLEngine}s which have completed their handshake in memory.
 * <p>
 * The key store, holding a self-signed certificate, is generated, before all tests, by the
 * {@code keytool} of the running JDK.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("wrap(engine, dst) / extras")
@Slf4j
class HelloWorld_Wrap_SSLEngine_Dst__Test extends HelloWorld__Test {

    private static final String PASSWORD = "changeit";

    /**
     * The {@link TempDir} shared by tests in this class.
     */
    @TempDir
    private static Path tempDir;

    private static SSLContext context;

    /**
     * Generates a key store with the {@code keytool} of the running JDK, and initializes an
     * {@link SSLContext} with it, as both the key store and the trust store.
     *
     * @throws Exception if failed to generate the key store, or to initialize the context.
     */
    @BeforeAll
    static void __createContext() throws Exception {
        final var bin = Path.of(System.getProperty("java.home"), "bin");
        final var keytool = Files.exists(bin.resolve("keytool"))
                ? bin.resolve("keytool")
                : bin.resolve("keytool.exe");
        Assumptions.assumeTrue(Files.isExecutable(keytool), "no keytool: " + keytool);
        final var keystore = tempDir.resolve("keystore.p12");
        final var process = new ProcessBuilder(
                keytool.toString(), "-genkeypair",
                "-alias", "hello",
                "-keyalg", "EC", "-groupname", "secp256r1",
                "-dname", "CN=localhost",
                "-validity", "1",
                "-storetype", "PKCS12",
                "-keystore", keystore.toString(),
                "-storepass", PASSWORD,
                "-keypass", PASSWORD
        ).redirectErrorStream(true).start();
        final var output = new String(process.getInputStream().readAllBytes(),
                                      StandardCharsets.UTF_8);
        assertTrue(process.waitFor(1L, TimeUnit.MINUTES), "keytool timed out");
        assertEquals(0, process.exitValue(), output);
        final var store = KeyStore.getInstance("PKCS12");
        try (var stream = Files.newInputStream(keystore)) {
            store.load(stream, PASSWORD.toCharArray());
        }
        final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(store, PASSWORD.toCharArray());
        final var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(store);
        context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }

    private static void runDelegatedTasks(final SSLEngine engine) {
        for (Runnable task; (task = engine.getDelegatedTask()) != null; ) {
            task.run();
        }
    }

    /**
     * Performs the handshake between the specified client and server engines, in memory.
     *
     * @param client the client engine.
     * @param server the server engine.
     * @throws SSLException if an SSL error occurs.
     */
    private static void handshake(final SSLEngine client, final SSLEngine server)
            throws SSLException {
        final var empty = ByteBuffer.allocate(0);
        final var c2s = ByteBuffer.allocate(client.getSession().getPacketBufferSize());
        final var s2c = ByteBuffer.allocate(server.getSession().getPacketBufferSize());
        final var clientIn = ByteBuffer.allocate(client.getSession().getApplicationBufferSize());
        final var serverIn = ByteBuffer.allocate(server.getSession().getApplicationBufferSize());
        client.beginHandshake();
        server.beginHandshake();
        for (var i = 0; ; i++) {
            if (client.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING
                && server.getHandshakeStatus()
                   == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                return;
            }
            assertTrue(i < 64, "handshake did not finish");
            client.wrap(empty, c2s);
            runDelegatedTasks(client);
            server.wrap(empty, s2c);
            runDelegatedTasks(server);
            c2s.flip();
            s2c.flip();
            client.unwrap(s2c, clientIn);
            runDelegatedTasks(client);
            server.unwrap(c2s, serverIn);
            runDelegatedTasks(server);
            c2s.compact();
            s2c.compact();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.wrap(engine, dst)} to wrap the {@code hello-world-bytes} into the
     * {@code dst} before each test.
     *
     * @throws SSLException if an SSL error occurs.
     */
    @BeforeEach
    void __stubService() throws SSLException {
        doAnswer(i -> {
            final var engine = i.getArgument(0, SSLEngine.class);
            final var dst = i.getArgument(1, ByteBuffer.class);
            for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                engine.wrap(b, dst);
            }
            return engine;
        }).when(service()).wrap(notNull(), notNull());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@code hello-world-bytes}, wrapped by the client engine, are unwrapped,
     * as they are, by the server engine.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("real SSLEngine pair")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var client = context.createSSLEngine("localhost", 443);
        client.setUseClientMode(true);
        final var server = context.createSSLEngine();
        server.setUseClientMode(false);
        handshake(client, server);
        final var session = client.getSession();
        final var dst = ByteBuffer.allocate(session.getPacketBufferSize());
        // ------------------------------------------------------------------------------------ when
        service().wrap(client, dst);
        // ------------------------------------------------------------------------------------ then
        dst.flip();
        log.debug("protocol: {}, cipher suite: {}", session.getProtocol(),
                  session.getCipherSuite());
        log.debug("wrapped: {} bytes -> {} bytes", HelloWorld.BYTES, dst.remaining());
        final var app = ByteBuffer.allocate(server.getSession().getApplicationBufferSize());
        final var result = server.unwrap(dst, app);
        log.debug("unwrapped: {}", result);
        assertEquals(SSLEngineResult.Status.OK, result.getStatus());
        assertEquals(hello_world_string(), StandardCharsets.US_ASCII.decode(app.flip()).toString());
    }
}
