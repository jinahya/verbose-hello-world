#!/usr/bin/python3

import hashlib
import random
import socket
import sys
from base64 import b64encode
from contextlib import closing

IPv4 = True
FAMILY = socket.AF_INET if IPv4 else socket.AF_INET6
HOST = '127.0.0.1' if IPv4 else "::1"
PORT = 50007


def hook(event, args):
    print(f'\t[audit]: {event} with args={args}')


sys.addaudithook(hook)


# https://stackoverflow.com/a/77426018/330457
def randomize(a):
    a[:] = random.randbytes(len(a))


with closing(socket.socket(FAMILY, socket.SOCK_STREAM)) as s:
    # -------------------------------------------------------------------- bind
    if bool(random.getrandbits(1)):
        s.bind((HOST, 0))
        print(f'(optionally) bound to {s.getsockname()}')
    # ----------------------------------------------------------------- connect
    s.connect((HOST, PORT))
    print(f'connected to {s.getpeername()}')
    bytes_ = random.randint(0, 65536)
    print(f'sending {bytes_} byte(s)')
    array = bytearray(random.randint(1024, 8192))
    print(f'array.length: {len(array)}')
    hash_ = hashlib.sha256()
    while bytes_ > 0:
        # ---------------------------------------------------------------- send
        randomize(array)
        if bytes_ < len(array):
            array = array[0:bytes_]
        w = s.send(array)
        hash_.update(array)
        bytes_ -= w
        # ---------------------------------------------------------------- recv
        bytes_received = s.recv(len(array))
    # ---------------------------------------------------------------- shutdown
    s.shutdown(socket.SHUT_WR)
    # ------------------------------------------------------------- read-to-eof
    while True:
        bytes_received = s.recv(len(array))
        if len(bytes_received) == 0:
            break
    # ------------------------------------------------------------ print-digest
    print(f'digest: {b64encode(hash_.digest()).decode()}')
