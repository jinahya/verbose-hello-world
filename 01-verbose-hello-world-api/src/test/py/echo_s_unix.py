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
import os
import socket
import tempfile
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


if not hasattr(socket, "AF_UNIX"):
    raise SystemExit("AF_UNIX is not available on this platform")

path = os.path.join(tempfile.mkdtemp(), "echo.sock")
try:
    with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as server:
        server.bind(path)
        server.listen(1)
        address = server.getsockname()
        print(f"[server] bound: {address}")
        threading.Thread(target=serve, args=(server,)).start()
        with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as client:
            client.connect(path)
            print(f"[client] connected to : {path}")
            client.sendall(HELLO_WORLD)
            dst = bytearray()
            while len(dst) < BYTES:
                chunk = client.recv(BYTES - len(dst))
                assert chunk, "premature EOF"
                dst.extend(chunk)
            assert bytes(dst) == HELLO_WORLD, f"unexpected: {bytes(dst)!r}"
            print(f"[client] received: {bytes(dst)!r}")
finally:
    if os.path.exists(path):
        os.unlink(path)
    os.rmdir(os.path.dirname(path))
