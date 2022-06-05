


from multiprocessing import connection


class JavaObj:
    def __init__(self, connection, javaRef: int, jClass: str, pyRef: int):
        self.connection = connection
        self.__javaRef = javaRef
        self.__pyRef = pyRef
        self.__class = jClass

    def __getattribute__(self, name: str) -> Any:
        pass

    def __hash__(self) -> int:
        return self.__ref

class JavaItterator( JavaObj ):
    def __init__(self, connection, javaRef: int, jClass: str, pyRef: int):
        self.connection = connection
        self.__javaRef = javaRef
        self.__pyRef = pyRef
        self.__class = jClass

    def __next__(self):

        pass # todo


def itteratorToGen( javaObj:JavaObj ):
    while javaObj.hasNext():
        yield javaObj.next()