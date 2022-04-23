import json
import struct

class JsonSocket:

    def __init__(self, socket):
        self.socket = socket
    

    def send(self, jsonObj):
        self.sendall( json.dumps(jsonObj).encode() )

    def _recv(self,  n ):
        buf = bytes()
        while n > 0:
            chunk = self.socket.recv(n)
            n -= len(chunk)
            buf += chunk
        return buf

    def receive(self):
        msgLen = struct.unpack(">i",self._recv(4))
        msg = self._recv(msgLen).decode()
        return json.loads(msg)
        

