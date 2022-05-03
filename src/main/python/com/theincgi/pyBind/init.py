import asyncio
import subprocess
from sys import argv, stderr
import socket
import socketserver
from JsonSocket import JsonSocket

connection = None
refs = {}
_running = False

# Java is hosting socket, python connects
def connectToJava( port ):
    global connection
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    connection = JsonSocket( s.connect(('127.0.0.1',port)) )
    print("Python connected")

def runMessageHandler():
    if not connection:
        raise Exception("Not connected")
    _running = True
    while _running:
        msg = connection.receive()
        op = msg["op"]
        
        if op == "GET":
            pass
        elif op == "SET":
            pass
        elif op == "CALL":
            pass
        elif op == "EXEC":
            exec( msg["py"] )
        elif op == "EVAL":
            id = msg["id"]
            code = msg["py"]
            codeLines = code.split("\n")
            wrapped = "def foo():"
            for line in codeLines:
                wrapped += "\t%s\n" % (line,)
            wrapped += "results = foo()"
            env = {}
            exec(wrapped, globals(), env)
            results = env["results"]
            connection.sendResult( id, results )
        elif op == "ERR":
            pass
        else:
            pass


def main(port: int):
    print("Connecting to java on port %d" % (port,))
    print("This is an error check",file=stderr)
    connectToJava(port)


print("PYTHON")
if __name__ == "__main__":
    main( int(argv[1]) )
