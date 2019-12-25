#!/usr/bin/env python3

import socket
import sys

host = sys.argv[1]
port = sys.argv[2]

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((host, int(port)))
    data = s.recv(12)
    print(data.decode("ascii"))
    s.close()
