import asyncio
import socket

from echo_ import HELLO_WORLD, BYTES


async def serve(reader, writer):
    data = await reader.readexactly(BYTES)
    writer.write(data)
    await writer.drain()
    writer.close()
    await writer.wait_closed()


async def main():
    if not socket.has_ipv6:
        raise SystemExit("IPv6 is not available on this platform")
    server = await asyncio.start_server(
        serve, host="::1", port=0, family=socket.AF_INET6
    )
    address = server.sockets[0].getsockname()
    print(f"[server] bound: {address}")
    async with server:
        reader, writer = await asyncio.open_connection(
            host=address[0], port=address[1], family=socket.AF_INET6
        )
        print(f"[client] connected to : {writer.get_extra_info('peername')}")
        writer.write(HELLO_WORLD)
        await writer.drain()
        dst = await reader.readexactly(BYTES)
        assert dst == HELLO_WORLD, f"unexpected: {dst!r}"
        print(f"[client] received: {dst!r}")
        writer.close()
        await writer.wait_closed()


asyncio.run(main())
