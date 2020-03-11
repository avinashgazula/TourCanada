__author__ = "Daksh Patel"
from flask import *
from project import app
from utils import *
from wtforms import *
from project.model.forms import RegistrationForm
from project.model import dynamodb, scan_table, DecimalEncoder


@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        print(request.form)
        username=request.form.get('username')
        password=request.form.get('password')
        name=request.form.get('name')
        email=request.form.get('email')
        table = dynamodb.Table('users')
        response=table.put_item(
            Item={
                'username':username,
                'name':name,
                'email':email,
                'password':password
            }
        )
        print(json.dumps(response, indent=4, cls=DecimalEncoder))
        if response['ResponseMetadata']['HTTPStatusCode']==200:
            resp = {
                'success': True
            }
            return make_response(jsonify(resp), 201)

    return render_template('register.html')

@app.route('/register/authenticate',methods=['GET', 'POST'])
def registrationAuthenticate():
    if request.method=='POST':
        pass
    else:
        email=request.args.get('email')
        username=request.args.get('username')
        print(request.args)
        final_email=encryptEmail(email)

        print(final_email)
        return render_template('2fa.html', email=final_email, username=username)