from flask import Flask, jsonify, request, g
import boto3
aws_access_key_id = 'AKIA5WTK4BJPARQ7JDBC'
aws_secret_access_key  = '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'
region = 'us-east-2'



app = Flask(__name__)

@app.before_request
def before_request():
    print("hello world")
    g.session = boto3.Session(
        aws_access_key_id=aws_access_key_id,
        aws_secret_access_key=aws_secret_access_key,
        region_name=region, 
    )


    g.dynamodb = g.session.resource('dynamodb',
    aws_access_key_id=aws_access_key_id,
        aws_secret_access_key=aws_secret_access_key,
        region_name=region, )



@app.route('/', methods=['GET', 'POST'])
def hello():
    if request.method == 'POST':
        x = request.get_json()
        return jsonify({'sent': x}), 201
    else:
        return jsonify({'sent':'nothing'})

@app.route('/multi/', methods=['GET'])
def get_mul():
    num = int(request.args.get("num"))
    print("num", num)
    return jsonify({'result':num*10})

# get a list of locations
@app.route('/locations/', methods=['GET'])
def get_locations():
    table = g.dynamodb.Table('locations')
    table_response = table.scan()
    item_list = table_response['Items']
    locations = [item['location'] for item in item_list]
    return jsonify({'result':locations})

@app.route('/destinations/<string:location>', methods=['GET'])
def get_destinations(location):
    table = g.dynamodb.Table('key_destinations')
    table_response = table.scan()
    item_list = table_response['Items']
    for item in item_list:
        item.pop('id', None)
        item.pop('location', None)
    return jsonify({'result':item_list})

if __name__ == "__main__":
    app.run(debug=True)