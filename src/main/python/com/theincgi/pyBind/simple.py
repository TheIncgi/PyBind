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
    