###
# #%L
# verbose-hello-world-api
# %%
# Copyright (C) 2018 - 2026 Jinahya, Inc.
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###
import socket
import threading

from echo_ import HELLO_WORLD, BYTES


def serve(server):
    accepted, _ = server.accept()
    with accepted:
        buf = bytearray()
        while len(buf) < BYTES:
            chunk = accepted.recv(BYTES - len(buf))
            assert chunk, "premature EOF"
            buf.extend(chunk)
        accepted.sendall(bytes(buf))


if not socket.has_ipv6:
    raise SystemExit("IPv6 is not available on this platform")

with socket.socket(socket.AF_INET6, socket.SOCK_STREAM) as server:
    server.bind(("::1", 0))
    server.listen(1)
    address = server.getsockname()
    print(f"[server] bound: {address}")
    threading.Thread(target=serve, args=(server,)).start()
    with socket.socket(socket.AF_INET6, socket.SOCK_STREAM) as client:
        client.connect(address)
        print(f"[client] connected to : {client.getpeername()}")
        client.sendall(HELLO_WORLD)
        dst = bytearray()
        while len(dst) < BYTES:
            chunk = client.recv(BYTES - len(dst))
            assert chunk, "premature EOF"
            dst.extend(chunk)
        assert bytes(dst) == HELLO_WORLD, f"unexpected: {bytes(dst)!r}"
        print(f"[client] received: {bytes(dst)!r}")
