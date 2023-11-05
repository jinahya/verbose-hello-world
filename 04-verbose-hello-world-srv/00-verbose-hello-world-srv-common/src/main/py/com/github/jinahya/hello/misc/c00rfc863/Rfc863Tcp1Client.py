#!/usr/bin/python3

import hashlib
import random
import socket
import sys
from contextlib import closing

HOST = '127.0.0.1'
PORT = 50009


def hook(event, args):
    if event == 'socket.bind':
        print(f'audit: {event} with args={args}')
    if event == 'socket.connect':
        print(f'audit: {event} with args={args}')


sys.addaudithook(hook)


def randomize(array):
    for i in range(len(array)):
        array[i] = random.randint(0, 255)


with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:
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
        # --------------------------------------------------------------- send
        w = s.send(array)
        hash.update(array)
        bytes -= w
    print(f'digest: {hash.hexdigest()}')
