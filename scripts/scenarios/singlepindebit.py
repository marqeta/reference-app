import requests
import json
import base64
import os
import sys

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from common.util import *
from scenarios import __version__

session = requests.Session()
session.headers.update({
    'User-Agent': 'marqeta-reference-app:' + __version__
})

base_url,application_token,access_token = get_sandbox_details()
request_url = f"{base_url}/simulations/cardtransactions/pindebit"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Basic {base64.b64encode((application_token + ':' + access_token).encode()).decode()}"
}
amount = 12.0
payload = {
    "card_token": "reference_app_card_1",
    "amount": 10,
    "network": "VISA",
    "card_acceptor": {
        "mid": "11111"
    },
}

# Simulating PINDEBIT transaction
print("Simulating single-message PIN debit transaction for amount:", 10 )
response = session.post(request_url, headers=headers, data=json.dumps(payload))
response_data = response.json()
token1 = response_data["transaction"]["token"]
print("Response code: ", response.status_code , "token: ",token1)