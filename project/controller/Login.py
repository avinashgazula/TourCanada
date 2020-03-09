__author__ = "Daksh Patel"

from flask import *
from project import app
from project.model.LoginModel import Login
from project.model.LocationModel import Location


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
		location=Location()
		key = 'user'
		user = login.getUserDetails(session.get(key)).get('Items')[0]
		trendingLocations=location.getLocations()
		print(trendingLocations)
		trendingLocations=sorted(trendingLocations, key=lambda t: t['id'])[:6]
		# rows=len(trendingLocations)//3
		finalTrends=[]
		temp=[]
		print('all',trendingLocations)
		for i in range(len(trendingLocations)):
			# print(i)
			if (i%3==0 and i!=0):
				finalTrends.append(temp)
				temp=[]
			temp.append(trendingLocations[i])
			if i == len(trendingLocations) - 1:
				finalTrends.append(temp)
				temp = []
		print('finalTrends',finalTrends)

		print(user)
		return render_template('home.html', user=user, trends=finalTrends)
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
