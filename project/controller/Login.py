__author__ = "Daksh Patel"

from flask import *
from project import app
from project.model.LoginModel import Login

@app.route('/')
def index():
	if session.get('user'):
		return redirect(url_for('home'))
	else:
		return redirect(url_for('login'))
		# return render_template('index.html')

@app.route('/home')
def home():
	if session.get('user'):
		login = Login()
		key = 'user'
		response = login.getUserDetails(session.get(key)).get('Items')[0]
		print(response)
		return render_template('home.html', resp=response)
	else:
		return redirect(url_for('login'))

@app.route('/login', methods=['POST', 'GET'])
def login():
	if session.get('user'):
		return redirect(url_for('home'))
	if request.method=='POST':
		login = Login()
		username=request.form.get('username')
		password = request.form.get('password')
		matched, error = login.checkUserCredentials(username=username, password=password)
		if matched:
			session['user']=username
			return redirect(url_for('home'))
		else:
			print(error)
			return render_template('index.html', error=error)
	else:
		return render_template('index.html')

@app.route('/register')
def register():
	if session.get('user'):
		return redirect(url_for('home'))
	else:
		return render_template('register.html')
