__author__ = "Daksh Patel"

from flask import *
from project import app

@app.route('/')
def index():
	if session.get('user'):
		return render_template('home.html')
	else:
		return redirect(url_for('login'))
		# return render_template('index.html')

@app.route('/login')
def login():
	if session.get('user'):
		return render_template('home.html')
	else:
		return render_template('index.html')

@app.route('/register')
def register():
	if session.get('user'):
		return render_template('home.html')
	else:
		return render_template('register.html')
