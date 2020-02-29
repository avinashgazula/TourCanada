__author__ = "Daksh Patel"

from flask import *
from project import app

@app.route('/')
def index():
	return render_template('test.html')
