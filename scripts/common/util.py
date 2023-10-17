import os
import sys
from urllib.parse import urljoin
from common.currency import *
import requests
import json
import base64


# Checks if all required env are set
def get_sandbox_details():
    sandbox_url = os.environ.get('SANDBOX_URL')
    if sandbox_url is None:
        print("Environment variable 'SANDBOX_URL' is not set. Exiting...")
        sys.exit(1)
    if sandbox_url.endswith('/'):
        sandbox_url = sandbox_url[:-1]

    application_token = os.environ.get('APPLICATION_TOKEN')
    if application_token is None:
        print("Environment variable 'APPLICATION_TOKEN' is not set. Exiting...")
        sys.exit(1)

    access_token = os.environ.get('ACCESS_TOKEN')
    if access_token is None:
        print("Environment variable 'ACCESS_TOKEN' is not set. Exiting...")
        sys.exit(1)
    return urljoin(sandbox_url, 'v3'), application_token, access_token

def get_ngrok_url():
    ngrok_url = os.environ.get('NGROK_URL')
    if ngrok_url is None:
        print("Environment variable 'NGROK_URL' is not set. Exiting...")
        sys.exit(1)
    return ngrok_url

def fire_dummy_transaction():
    base_url, application_token, access_token = get_sandbox_details()
    request_url = f"{base_url}/simulations/cardtransactions/authorization"
    headers = {"Content-Type": "application/json",
               "Authorization": f"Basic {base64.b64encode((application_token + ':' + access_token).encode()).decode()}"}
    amount = 1.0
    payload = {"amount": amount, "card_token": "reference_app_card_1",
               "card_acceptor": {"zip": "94115", "address": "1899 Fillmore St", "city": "San Francisco",
                                 "name": "Walgreens Pharmacy", "state": "CA", "mid": "1235"}, }
    response = requests.post(request_url, headers=headers, data=json.dumps(payload))
    response_data = response.json()
    return response_data["transaction"]["token"]

