import requests
import json
import base64
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from common.util import *
from scenarios import __version__

session = requests.Session()
session.headers.update({
    'User-Agent': 'marqeta-reference-app:' + __version__
})

base_url, application_token, access_token = get_sandbox_details()

request_url = f"{base_url}/simulations/cardtransactions/authorization"
headers = {
    "Content-Type": "application/json",
    "Authorization": f"Basic {base64.b64encode((application_token + ':' + access_token).encode()).decode()}"
}

amount = 50.00
payload = {
    "amount": amount,
    "card_token": "reference_app_card_1",
    "card_acceptor": {
        "mid": "1235"
    },
}

# Simulating Authorization transaction
print("Simulating authorization transaction for amount:", amount)
response = session.post(request_url, headers=headers, data=json.dumps(payload))
response_data = response.json()
token1 = response_data["transaction"]["token"]
print("Response code: ", response.status_code, "token: ", token1)

amount = 25.00
print("Simulating authorization clearing transaction for amount:", amount)
request_url = f"{base_url}/simulations/cardtransactions/authorization.clearing"
payload = {
    "amount": amount,
    "preceding_related_transaction_token": token1,
    "card_token": "reference_app_card_1",
    "mid": "1235",
    "card_acceptor": {
        "address": "1899 Fillmore St",
    }
}

response = session.post(request_url, headers=headers, data=json.dumps(payload))
response_data = response.json()
token2 = response_data["transaction"]['token']
print("Response code: ", response.status_code, "token: ", token2)

print("Simulating authorization reversal for amount:", amount)
request_url = f"{base_url}/simulations/cardtransactions/authorization.reversal"
payload = {
    "amount": 25,
    "preceding_related_transaction_token": token1,
    "card_token": "reference_app_card_1",
}

response = session.post(request_url, headers=headers, data=json.dumps(payload))
response_data = response.json()
token2 = response_data["transaction"]['token']
print("Response code: ", response.status_code, "token: ", token2)