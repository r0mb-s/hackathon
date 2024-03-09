from flask import Flask
from flask import request

app = Flask(__name__)

@app.route("/")
def hello_world():
    return "<p>Hello, World!</p>"

@app.route('/data', methods=['POST'])
def get_numar():
    numar = request.get_json()
    if numar is not None:
        # nr = numar.get('numar')
        print(numar)
        return "merge"

    
    # Your login logic here