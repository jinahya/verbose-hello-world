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
