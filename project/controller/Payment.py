__author__ = "Amogh Adithya"

from flask import Flask, render_template, url_for, flash, redirect
from project.model.forms import  PaymentForm
from project import app


@app.route('/payment', methods=['GET', 'POST'])
def payment():
    print('here')
    form = PaymentForm()
    print(form.validate_on_submit())
    if form.validate_on_submit():
        flash(f'payment made for {form.Cardname.data}!', 'success')
        return redirect(url_for('ticketgen'))
    else:
        pass
    return render_template('payment.html', title='payment', form=form)
