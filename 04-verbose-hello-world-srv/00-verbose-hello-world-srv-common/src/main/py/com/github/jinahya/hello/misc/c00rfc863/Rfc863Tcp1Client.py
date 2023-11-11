#!/usr/bin/python3

import hashlib
import random
import socket
import sys
from contextlib import closing

IPv4 = True
FAMILY = socket.AF_INET if IPv4 else socket.AF_INET6
HOST = '127.0.0.1' if IPv4 else "::1"
PORT = 50009


def hook(event, args):
    print(f'\t[audit]: {event} with args={args}')


sys.addaudithook(hook)


# https://stackoverflow.com/a/77426018/330457
def randomize(array):
    array[:] = random.randbytes(len(array))


with closing(socket.socket(FAMILY, socket.SOCK_STREAM)) as s:
    # -------------------------------------------------------------------- bind
    if bool(random.getrandbits(1)):
        s.bind((HOST, 0))
        print(f'(optionally) bound to {s.getsockname()}')
    # ----------------------------------------------------------------- connect
    s.connect((HOST, PORT))
    print(f'connected to {s.getpeername()}')
    bytes = random.randint(0, 65536)
    print(f'sending {bytes} byte(s)')
    array = bytearray(random.randint(1, 8192))
    print(f'array.length: {len(array)}')
    hash = hashlib.sha1()
    while bytes > 0:
        # random.shuffle(array)
        randomize(array);
        if bytes < len(array):
            array = array[0:bytes]
        # ---------------------------------------------------------------- send
        w = s.send(array)
        hash.update(array)
        bytes -= w
    print(f'digest: {hash.hexdigest()}')
