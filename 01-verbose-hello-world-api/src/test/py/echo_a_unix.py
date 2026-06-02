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
import asyncio
import os
import socket
import tempfile

from echo_ import HELLO_WORLD, BYTES


async def serve(reader, writer):
    data = await reader.readexactly(BYTES)
    writer.write(data)
    await writer.drain()
    writer.close()
    await writer.wait_closed()


async def main(path):
    server = await asyncio.start_unix_server(serve, path=path)
    print(f"[server] bound: '{path}'")
    async with server:
        reader, writer = await asyncio.open_unix_connection(path=path)
        print(f"[client] connected to : '{path}'")
        writer.write(HELLO_WORLD)
        await writer.drain()
        dst = await reader.readexactly(BYTES)
        assert dst == HELLO_WORLD, f"unexpected: {dst!r}"
        print(f"[client] received: {dst!r}")
        writer.close()
        await writer.wait_closed()


if not hasattr(socket, "AF_UNIX"):
    raise SystemExit("AF_UNIX is not available on this platform")

path = os.path.join(tempfile.mkdtemp(), "echo.sock")
try:
    asyncio.run(main(path))
finally:
    if os.path.exists(path):
        os.unlink(path)
    os.rmdir(os.path.dirname(path))
