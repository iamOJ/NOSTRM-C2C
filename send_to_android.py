from flask import Flask
import json

app = Flask(__name__)

@app.route('/mi', methods=['GET'])
def echo_msg():
    pythonDictionary = {"cc" :
    						[
    							{'name': 'Bob', 'age': 44, 'aa': True},
    							{'name': 'y', 'age': 42, 'aa': False},
    							{'name': 'Boy', 'age': 42, 'aa': False},
    							{'name': 'bby', 'age': 42, 'aa': False}
    						]
    					}
    dictionaryToJson = json.dumps(pythonDictionary)
    return dictionaryToJson

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=4100)