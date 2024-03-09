import asyncio
import aiohttp
import random

async def post_data(session, url, sensor_id):
    data = {
        'id': sensor_id,
        'timestamp': 0,
        'distance': random.uniform(3, 400)
    }
    async with session.post(url, json=data) as response:
        if response.status != 200:
            print(f"Error posting data for sensor {sensor_id}: {response.status}")

async def main():
    sensors = 20
    url = 'http://192.168.222.153:5000/data'
    async with aiohttp.ClientSession() as session:
        while True:
            tasks = [post_data(session, url, i) for i in range(2, sensors)]
            await asyncio.gather(*tasks)
            await asyncio.sleep(random.randint(1,3)) 

if __name__ == "__main__":
    asyncio.run(main())

