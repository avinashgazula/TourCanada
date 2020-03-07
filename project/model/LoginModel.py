__author__ = "Daksh Patel"
from pymongo import MongoClient
import copy


class Login:
    def __init__(self):
        self.client = MongoClient()
        self.db = self.client.tourcanada
        self.table = self.db.users

    def getUserCredentials(self, username):
        error = None
        rows = self.table.find({"username": "{}".format(username)})
        row_count = self.table.count_documents({"username": "{}".format(username)})
        if row_count>0:
            error = 'Username doesnot exists'
            return tuple(rows), error
        else:
            return rows