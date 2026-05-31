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


if not hasattr(socket, "AF_INET"):
    raise SystemExit("AF_INET is not available on this platform")

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server:
    server.bind(("127.0.0.1", 0))
    server.listen(1)
    address = server.getsockname()
    print(f"[server] bound: {address}")
    threading.Thread(target=serve, args=(server,)).start()
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client:
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
