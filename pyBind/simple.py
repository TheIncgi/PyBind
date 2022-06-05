import re


print("loaded simple")

def add(a, b):
    return a+b

def concat(*args):
    out = ""
    for a in args:
        out += a
    return out

def notBool( a ):
    return not a

def reverseList( l ):
    copy = l.copy()
    copy.reverse()
    return copy

def dictReverse( d ):
    out = {}
    for k,v in d.items():
        out[v] = k
    return out

def callThat( f, *args ):
    return f(*args)

def sampleIterator( it, times ):
    out = []
    for i in range(0,times):
        out.append( next( it ) )
    return out

def testObj( obj, value ):
    v = obj.getX()
    v = v + value
    obj.setX( v )
