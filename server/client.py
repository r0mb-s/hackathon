from flask import Flask
from flask import request

import edit_db

request_counter = 0
car_counter = 0

app = Flask(__name__)

@app.route("/")
def hello_world():
    return "<p>Hello, World!</p>"

@app.route('/data', methods=['POST'])
def get_numar():
    numar = request.get_json()
    if numar is not None:
        handle_data(numar.get("id"), numar.get("timestamp"), numar.get("distance"))
    return '', 200
    
def handle_data(id, time, distance):
    global request_counter, car_counter

    request_counter += 1

    if distance < 250:
        car_counter += 1
    if request_counter == 10:
        print(car_counter / request_counter)
        edit_db.functie(id, car_counter / request_counter)
        request_counter = 0
        car_counter = 0