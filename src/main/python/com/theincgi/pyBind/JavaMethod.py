from JavaObj import JavaObj

class JavaMethod:
    def __init__(self, javaObject: JavaObj):
        self.obj = javaObject
        pass
    
    def call( self, *args: any, signature:list =None, varArgs=False ):
        self.obj.connection.send()
        pass

    def __call__(self, *args: any, **kwds: any) -> any:

        pass

