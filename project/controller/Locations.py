__author__ = "Daksh Patel"

from flask import *
from project.model.forms import  *
from project import app
from project.model.LoginModel import Login
from project.model.LocationModel import Location
from utils import *


@app.route('/destinations', methods=['GET'])
def destinations():
    request_from = request.args.get('request_from')
    if request_from == 'mobile':
        location = Location()
        login = Login()
        key = 'user'
        user = login.getUserDetails(session.get(key)).get('Items')[0]
        loc = request.args.get('location').lower()
        destinations_loc = capitalizeAll(location.getDestinations(loc))
        finalDestinations = []
        temp = []
        # print('all', destinations_loc)
        for i in range(len(destinations_loc)):
            # print(i)
            if (i % 3 == 0 and i != 0):
                finalDestinations.append(temp)
                temp = []
            temp.append(destinations_loc[i])
            if i == len(destinations_loc) - 1:
                finalDestinations.append(temp)
                temp = []
        # print('finalTrends', finalDestinations)
        print(finalDestinations)
        resp = json.dumps(finalDestinations, indent=2)
        encryptedResp = None
        return resp
    else:
        location = Location()
        login = Login()
        key = 'user'
        user = login.getUserDetails(session.get(key)).get('Items')[0]
        loc = request.args.get('location').lower()
        destinations_loc = capitalizeAll(location.getDestinations(loc))
        finalDestinations = []
        temp = []
        # print('all', destinations_loc)
        for i in range(len(destinations_loc)):
            # print(i)
            if (i % 3 == 0 and i != 0):
                finalDestinations.append(temp)
                temp = []
            temp.append(destinations_loc[i])
            if i == len(destinations_loc) - 1:
                finalDestinations.append(temp)
                temp = []
        # print('finalTrends', finalDestinations)
        print(finalDestinations)
        return render_template('destinations.html', user=user, trends=finalDestinations, loc=loc.title())

@app.route('/make_booking', methods=['GET', 'POST'])
def make_booking():
    form = PaymentForm()
    return render_template('payment.html', form=form, title='Payment')