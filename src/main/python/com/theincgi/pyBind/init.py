import asyncio
import subprocess
from sys import argv
import socket
import socketserver

connection = None
refs = {}

# Java is hosting socket, python connects
def connectToJava( port ):
    global connection
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    connection = s.connect(('locahost',port))
    print("Python connected")

# act as socket host and launch jvm
def launchJava(file, javaCmd=["java","-jar"]):
    global connection
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('127.0.0.1', 0))
    port = server.gettsockname()[1]
    server.listen(1)
    
    async def _():
        async def serve():
            return await server.accept()
        async def launch():
            args = javaCmd.copy()
            args.append(file)
            subprocess.Popen(args)
    
        return await asyncio.gather(serve, launch)
    socket, inet = asyncio.run(_())[1]
    connection = socket

def runMessageHandler():
    if not connection:
        raise Exception("Not connected")

def main(port: int):
    print("Connecting to java on port %d" % (port,), flush=True)
    connectToJava(port)


print("PYTHON", flush=True)
if __name__ == "__main__":
    main( int(argv[1]) )
