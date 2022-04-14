import asyncio
from time import sleep

async def test():
    print("a")

    async def f(i):
        print(i)
        if i==2:
            sleep(10)
        await asyncio.sleep(1)
        print("c")

    await asyncio.gather(f(1),f(2),f(3),f(4))

    print("d")
    
    


if __name__ == "__main__":
    asyncio.run(test())