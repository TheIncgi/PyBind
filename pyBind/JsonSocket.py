import json
import struct

class JsonSocket:

    def __init__(self, socket):
        if socket is None:
            raise Exception("JsonSocket received socket=None")
        
        self.socket = socket
    

    def send(self, jsonObj):
        print("dbug send: "+str(jsonObj))
        jsonObj = json.dumps(jsonObj)
        self.socket.sendall( struct.pack( ">i", len(jsonObj) ) )
        self.socket.sendall( jsonObj.encode() )
        print("Py -> (%d) %s" % (len(jsonObj), jsonObj))

    def sendResult(self, id, results):
        obj = {
            "id": id,
            "value": results
        }
        self.send( obj )

    def _recv(self,  n ):
        buf = bytes()
        while n > 0:
            chunk = self.socket.recv(n)
            n -= len(chunk)
            buf += chunk
        return buf

    def receive(self):
        msgLen = struct.unpack(">i",self._recv(4))[0]
        msg = self._recv(msgLen).decode()
        return json.loads(msg)
        

def isJsonSerializable(x):
    try:
        json.dumps(x)
        return True
    except (TypeError, OverflowError):
        return False