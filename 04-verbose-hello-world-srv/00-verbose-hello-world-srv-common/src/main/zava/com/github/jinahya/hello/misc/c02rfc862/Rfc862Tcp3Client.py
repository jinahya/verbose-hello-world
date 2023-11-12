#!/usr/bin/python3

import hashlib
import random
import select
import socket
import sys
from base64 import b64encode
from contextlib import closing

IPv4 = True
# IPv4 = False
FAMILY = socket.AF_INET if IPv4 else socket.AF_INET6
HOST = '127.0.0.1' if IPv4 else "::1"
PORT = 50007


def hook(event, args):
    print(f'\t[audit]: {event} with args={args}')


sys.addaudithook(hook)


# https://stackoverflow.com/a/77426018/330457
def randomize(array):
    array[:] = random.randbytes(len(array))


with closing(socket.socket(FAMILY, socket.SOCK_STREAM)) as s:
    # -------------------------------------------------------------------- bind
    if random.choice([True, False]):
        s.bind((HOST, 0))
        print(f'(optionally) bound to {s.getsockname()}')
    # ----------------------------------------------------------------- connect
    s.connect((HOST, PORT))
    print(f'connected to {s.getpeername()}')
    # --------------------------------------------------------------- configure
    s.setblocking(False)
    # ----------------------------------------------------------------- prepare
    total_bytes_to_send = random.randint(0, 65536)
    print(f'sending {total_bytes_to_send} byte(s)')
    array = bytearray(random.randint(1024, 8192))
    print(f'array.length: {len(array)}')
    hash_ = hashlib.sha256()
    # -------------------------------------------------------------------- loop
    rlist = [s]
    wlist = [s]
    while True:
        r_ready, w_ready, _ = select.select(rlist, wlist, [], 8.0)
        if s in w_ready:
            # ------------------------------------------------------------ send
            randomize(array)
            if total_bytes_to_send < len(array):
                array = array[0:total_bytes_to_send]
            number_of_bytes_written = s.send(array)
            hash_.update(array[0:number_of_bytes_written])
            total_bytes_to_send -= number_of_bytes_written
            if total_bytes_to_send == 0:
                # --------------------------------------------- shutdown-output
                s.shutdown(socket.SHUT_WR)
                wlist = []
        if s in r_ready:
            # ------------------------------------------------------------ recv
            bytes_received = s.recv(len(array))
            if len(bytes_received) == 0:
                break
    # ------------------------------------------------------------ print-digest
    print(f'digest: {b64encode(hash_.digest()).decode()}')
