


class JavaObj():
    def __init__(self, connection):
        self.connection = connection
        self.attribs = {}

    def __getattribute__(self, __name: str) -> Any:
        pass
