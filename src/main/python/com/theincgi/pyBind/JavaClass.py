

from JavaObj import JavaObj


class JavaClass:

    def __init__(self, connection, name):
        self._connection = connection
        self._name = name

    def new( *args ) -> JavaObj:
        pass
