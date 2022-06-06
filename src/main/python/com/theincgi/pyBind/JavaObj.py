


from multiprocessing import connection


class JavaObj:
    def __init__(self, connection, ref: int, jClass: str, ):
        self.connection = connection
        self.__ref = ref
        self.__class = jClass

    def __getattribute__(self, name: str) -> any:
        pass

    def __getitem__(self, name: str) -> any:
        pass

    def __hash__(self) -> int:
        return self.__ref

class JavaItterator( JavaObj ):
    def __init__(self, connection, ref: int, jClass: str):
        self.connection = connection
        self.__ref = ref
        self.__class = jClass

    def __next__(self):

        pass # todo


def itteratorToGen( javaObj:JavaObj ):
    while javaObj.hasNext():
        yield javaObj.next()