from locust import HttpLocust, TaskSet, task
from random import randint
import time, json

def randomTransaction():
    return { "amount": random(0, 100000), "timestamp": int(round(time.time() * 1000)) }

class SimpleBehavior(TaskSet):
    @task(1)
    def add_transaction(self):
      self.client.post("/transactions", data = json.dumps(self.random_transaction()), headers = { 'content-type': 'application/json' })

    @task(1)
    def get_statistics(self):
      self.client.get("/statistics")

    def random_transaction(self):
      return { 'amount': randint(0, 100000), 'timestamp': int(round(time.time() * 1000)) }

class ApiUser(HttpLocust):
    task_set = SimpleBehavior
    min_wait = 5000
    max_wait = 9000