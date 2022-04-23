import asyncio
import subprocess
from sys import argv
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

# act as socket host and launch jvm
def launchJava(file, javaCmd=["java","-jar"]):
    global connection
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('127.0.0.1', 0))
    port = server.getsockname()[1]
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
    connection = JsonSocket(socket)

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
    connectToJava(port)


print("PYTHON")
if __name__ == "__main__":
    main( int(argv[1]) )
