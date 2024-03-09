from flask import Flask, jsonify
from flask import request

import edit_db

request_counters, car_counters = edit_db.get_counters()
car_counter = 0

app = Flask(__name__)

@app.route('/data', methods=['POST'])
def get_numar():
    numar = request.get_json()
    if numar is not None:
        handle_data(numar.get("id"), numar.get("timestamp"), numar.get("distance"))
    return '', 200

@app.route('/location_and_percentage', methods=['GET'])
def percentage_route():
    result = edit_db.get_coordinates_and_percentage()
    return jsonify(result)
    
def handle_data(id, time, distance):
    global request_counters, car_counters

    request_counters[id] += 1

    if distance < 250:
        car_counters[id] += 1
    if request_counters[id] == 10:
        print(str(id) + " - " + str(request_counters[id]))
        edit_db.functie(id, car_counters[id] / request_counters[id])
        request_counters[id] = 0
        car_counters[id] = 0