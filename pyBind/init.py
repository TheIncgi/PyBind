import asyncio
import json
import subprocess
from sys import argv, stderr, modules
import socket
import socketserver
from tkinter import N
import traceback
from typing import OrderedDict
from JsonSocket import JsonSocket, isJsonSerializable
from JavaObj import JavaObj

connection = None

refs = {}


_running = False

def _nextRef():
    i=1
    while True:
        yield i
        i += 1
_nextRef = _nextRef()
def nextRef():
    return next(_nextRef)

def bindJava( ref, jClass ):
    key = "J:%d" % (ref,)
    if key in refs:
        return refs[key]["ref"]
    
    val = JavaObj( connection, ref, jClass )

    refs[ref] = val
    refs[key] = val

    return ref


def bind( module, name ):
    if module not in modules:
        mod = __import__( module, globals(), locals() )
    mod = modules[module]
    if not mod:
        raise Exception("could not import module '%%'" % (module,))

    if not hasattr( mod, name ):
        raise Exception("no attribute '%s' exists in module '%s'" % (name, module))

    key = "M:%s$%s" % (module, name)
    if key in refs:
        return refs[key]["ref"]
    
    def get():
        return getattr(mod, name)
    def set(v):
        setattr(mod, name, v)
    
    ref = nextRef()
    val = {
        'ref': ref,
        'key': key,
        'get': get,
        'set': set
    }

    refs[key] = val
    refs[ref] = val
    return ref

def unbind( ref ):
    val = refs.get( ref, None )
    if val:
        del refs[ val['key'] ]
        del refs[ val['ref'] ]

def bindReturn( value: any ) -> int:
    wrap = [value]
    ref = nextRef()
    key = "R:%d" % (ref)
    def get():
        return wrap[0]
    def set( v ):
        wrap[0] = v

    val = {
        'ref': ref,
        'key': key,
        'get': get,
        'set': set
    }

    refs[key] = val
    refs[ref] = val
    return ref

def serialize( value, ref=None ):

    if callable( value ):
        if not ref:
            ref = bindReturn( value )
        return {
            'type':"function",
            'ref':ref
        }
    if not(isJsonSerializable( value )):
        if not ref:
            ref = bindReturn( value )
        return {
            'type':"ref",
            'ref':ref
        }
    
    if isinstance( value, list ):
        v = []
        for x in value:
            v.append(serialize( x ))
        value = v
    
    if isinstance( value, dict ):
        v = {
            'keys':  [],
            'vals': []
        }
        for x,y in value.items():
            if isinstance(x,str):
                v['keys'].append( x )
            else:
                v['keys'].append( serialize( x ) )
            if isinstance(x,str):
                v['vals'].append( y )
            else:
                v['vals'].append( serialize( y ) )
        value = v

    rsp = {
        'type':type(value).__name__,
        'val': value
    }
    if isinstance(value, OrderedDict):
        rsp['type'] = "odict"
    if ref:
        rsp["ref"] = ref
    return rsp

def deserialize( jsonVal, evaluateRefs=True ):
    # print("Debug: deserialize | "+str(jsonVal))
    ty = jsonVal["type"]

    if ty == "NoneType":
        return None

    if ty == "int" or\
       ty == "float" or\
       ty == "str":
        return jsonVal["val"]
    
    if ty == "list":
        val = []
        for v in jsonVal["val"]:
            val.append( deserialize(v, evaluateRefs) )
        return val

    if ty == "dict" or ty=="odict":
        val = {} if ty=="dict" else OrderedDict()
        for k,v in zip(jsonVal["val"]["keys"], jsonVal["val"]["vals"]):
            if isinstance(k, str):
                val[k] = deserialize(v, evaluateRefs)
            else:
                val[deserialize(k, evaluateRefs)] = deserialize(v, evaluateRefs)
        return val
    
    if ty == "ref" or ty=="function":
        ref = refs[ jsonVal["ref"] ]
        if evaluateRefs:
            return ref["get"]
        return ref
    

    raise Exception("Type '%s' deserialization not implemented" % (ty,))
    
def deserializeArgs( args ):
    for i in range(0, len(args)):
        args[i] = deserialize( args[i] )


def deserializeKwargs( kwargs ):
    for k in kwargs.keys():
        kwargs[k] = deserialize( kwargs[k] )

# Java is hosting socket, python connects
def connectToJava( port ):
    global connection
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1',port))
    connection = JsonSocket( s )
    print("Python connected")

def wrapRef( refID, rtype="ref" ):
    return {
        "type": rtype,
        "ref": refID
    }

def runMessageHandler():
    if not connection:
        raise Exception("Not connected")
    print("receiving messages")
    _running = True
    while _running:
        msg = connection.receive()
        try:
            print("Py <- "+str(msg))
            op = msg["op"]
            rsp = {}

            if op == "GET":
                ref = msg["ref"]
                v = refs.get( ref, None )

                if not v:
                    raise Exception("No ref #%d" % (ref,))
                
                rsp = serialize( v["get"](), ref )

            elif op == "SET":
                ref = msg["ref"]
                v = refs.get( ref, None )

                if not v:
                    raise Exception("No ref #%d" % (ref,))
                
                v.set( deserialize( msg["value"] ) )

            elif op == "GET_ATTR":
                ref  = msg["ref"]
                name = msg["name"]
                asRef = msg["asRef"]

                obj = refs.get( ref, None )
                if not obj:
                    raise Exception("No ref #%d" % (ref,))

                val = getattr( obj, name )

                if asRef:
                    rsp = wrapRef( bindReturn( result ) )
                else:
                    rsp = serialize( result )

            elif op == "CALL":
                ref = refs.get(msg['ref'])["get"]()
                args = msg['args']
                kwargs = msg['kwargs']

                deserializeArgs(args)
                deserializeKwargs(kwargs)

                result = ref(*args, **kwargs)

                if msg["mode"] == "COPY":
                    rsp = serialize( result )
                elif msg["mode"] == "REF":
                    rsp = wrapRef( bindReturn( result ) )

                pass
            elif op == "BIND": # make python available to java
                lib = msg["lib"]
                name = msg["name"]
                ref = bind( module=lib, name=name )
                rsp = {
                    'type':'ref',
                    'ref':ref
                }
            elif op == "UNBIND": # delete ref of pyton resource
                ref = msg["ref"]
                unbind( ref )

            elif op == "TYPE":
                ref = msg["ref"]
                v = refs.get( ref, None )

                if not v:
                    raise Exception("No ref #%d" % (ref,))
                rsp = {
                    'type': type(v["get"]()).__name__
                }

            elif op == "EXEC":
                exec( msg["py"] )

            elif op == "EVAL":
                id = msg["id"]
                code = msg["py"]
                #todo args {}
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
            
            elif op == "MKREF":
                ref = nextRef()
                rsp = serialize( ref )

            elif op == "JBIND":
                ref    = msg["ref"]
                jclass = msg["class"]
                bindJava(ref, jclass)

            elif op == "JUNBIND": #python holds only reference to java stuff, GC'd when python is done with it
                pass

            elif op == "IS_CALLABLE":
                ref = msg["ref"]
                val = refs.get(ref, None)

                if isinstance( val, JavaObj ):
                    pass
                elif val:
                    val = val["get"]()

                rsp = serialize( val and callable( val ) )

            else:
                raise Exception("Unhandled op '%s'" % (op))
            
            if rsp:
                response = {
                    "id": msg["id"],
                    "msg": rsp
                }
                
                connection.send( response )
        except Exception as e:

            connection.send( {
                "op":"ERR",
                "msg":{
                    'from': msg['id'],
                    'msg': traceback.format_exc()
                }
            } )
    print("END MSG HANDLER")


def main(port: int):
    print("Connecting to java on port %d" % (port,))
    print("This is an error check",file=stderr)
    connectToJava(port)
    runMessageHandler()


print("PYTHON")
if __name__ == "__main__":
    main( int(argv[1]) )
    print("exited main")
