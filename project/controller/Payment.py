__author__ = "Amogh Adithya"

from flask import Flask, render_template, url_for, flash, redirect
import sys
sys.path.insert(0, './project/model')
from forms import  PaymentForm
from project import app

@app.route("/payment", methods=['GET', 'POST'])
def payment():
    form = PaymentForm()
    if form.validate_on_submit():
        flash(f'payment made for {form.Cardname.data}!', 'success')
        #return redirect(url_for('home'))
    return render_template('payment.html', title='payment', form=form)
