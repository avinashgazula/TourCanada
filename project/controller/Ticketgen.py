__author__ = "Amogh Adithya"

from flask import Flask, render_template, url_for, flash, redirect
import sys
# sys.path.insert(0, './project/model')
#from project.model.forms import  TicketForm
from project import app

@app.route("/ticketgen", methods=['GET', 'POST'])
def ticketgen():
    #form = TicketForm()
    
    return render_template('ticket.html', title='ticket')
