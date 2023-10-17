import base64
import os
import requests
import sys
import json
from urllib.parse import urljoin

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from common.util import get_sandbox_details
from common.util import *
from scenarios import __version__

session = requests.Session()
session.headers.update({
    'User-Agent': 'marqeta-reference-app:' + __version__
})

MANAGED_JIT_ENV = "MANAGED_JIT"

def check_payload(response):
    if response.status_code == 201 or response.status_code == 200:
        json_response = response.json()
        if 'active' in json_response and json_response['active'] == True:
            return True
    return False

def send_request(method, url, headers, data={}):
    if method == 'GET':
        response = session.get(url, headers=headers, json=data)
    elif method == 'POST':
        response = session.post(url, headers=headers, json=data)
    elif method == 'PUT':
        response = session.put(url, headers=headers, json=data)
    print(method , " " , url, " ", response.status_code)
    print(json.dumps(response.json(), indent=4))
    return check_payload(response)


base_url, application_token, access_token = get_sandbox_details()

ngrok_auth_password = os.environ.get('NGROK_AUTH_PASSWORD')
if ngrok_auth_password is None:
    print("Environment variable 'NGROK_AUTH_PASSWORD' is not set. Exiting...")
    sys.exit(1)

ngrok_auth_username = os.environ.get('NGROK_AUTH_USERNAME')
if ngrok_auth_username is None:
    print("Environment variable 'NGROK_AUTH_USERNAME' is not set. Exiting...")
    sys.exit(1)

ngrok_url = os.environ.get('NGROK_URL')
if ngrok_url is None:
    print("Environment variable 'NGROK_URL' is not set. Exiting...")
    sys.exit(1)

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Basic {base64.b64encode((application_token + ':' + access_token).encode()).decode()}"
}

print("Setting up fundingsources...")

funding_source_token = "reference_app_funding_gateway_token"
if MANAGED_JIT_ENV in os.environ and os.environ[MANAGED_JIT_ENV].lower() == "true":
    funding_source_token = "reference_app_funding_source_token"

if ("%s" % MANAGED_JIT_ENV) in os.environ and os.environ[MANAGED_JIT_ENV].lower() == "true":
    # Managed JIT
    fundingsource_url = f"{base_url}/fundingsources/program/"
    if not send_request("GET", fundingsource_url, headers):
        url = f"{base_url}/fundingsources/program/{funding_source_token}"
        post_data = {
            "name": "reference app demo funding source",
            "token": funding_source_token
        }
        send_request("POST", url, headers, post_data)
else:
    required_gateway_url = urljoin(ngrok_url, 'gateway')
    url = f"{base_url}/fundingsources/programgateway/{funding_source_token}"
    response = session.get(url, headers=headers, json={})
    print( "GET  ", url, " ", response.status_code)
    print(json.dumps(response.json(), indent=4))
    if check_payload(response):
        response_data = response.json()
        if response_data["url"] != required_gateway_url or response_data[
            "basic_auth_username"] != ngrok_auth_username or response_data[
            "basic_auth_password"] != ngrok_auth_password:
            data = {
                "url": required_gateway_url,
                "basic_auth_username": ngrok_auth_username,
                "basic_auth_password": ngrok_auth_password
            }
            send_request("PUT", url, headers, data)
    else:
        url = f"{base_url}/fundingsources/programgateway"
        data = {
            "name": "Reference app gateway ",
            "token": funding_source_token,
            "url": required_gateway_url,
            "basic_auth_username": ngrok_auth_username,
            "basic_auth_password": ngrok_auth_password
        }
        send_request("POST", url, headers, data)

print("Setting up  card product ...")
cardproduct_url = f"{base_url}/cardproducts/reference_app_card_product_token"
if not send_request("GET", cardproduct_url, headers):
    url = f"{base_url}/cardproducts"
    card_data = {
        "start_date": "2020-05-01",
        "token": "reference_app_card_product_token",
        "name": "Reference app card product",
        "config": {
            "fulfillment": {
                "payment_instrument": "VIRTUAL_PAN"
            },
            "poi": {
                "ecommerce": True,
                "atm": True
            },
            "card_life_cycle": {
                "activate_upon_issue": True
            }
        }
    }
    if MANAGED_JIT_ENV in os.environ and os.environ[MANAGED_JIT_ENV].lower() == "true":
        jit_obj = {"jit_funding": {
            "program_funding_source": {
                "funding_source_token": funding_source_token,
                "refunds_destination": "PROGRAM_FUNDING_SOURCE",
                "enabled": "true"
            }
        }}
    else:
        jit_obj = {"jit_funding": {
            "programgateway_funding_source": {
                "funding_source_token": funding_source_token,
                "refunds_destination": "GATEWAY",
                "enabled": "true"
            }
        }}
    config = card_data["config"]
    config.update(jit_obj)
    send_request("POST", url, headers, card_data)

print("Setting up user ...")
user_url = f"{base_url}/users/reference_app_user_1"
if not send_request("GET", user_url, headers):
    url = f"{base_url}/users"
    post_data = {
        "token": "reference_app_user_1"
    }
    send_request("POST", url, headers, post_data)

print("Setting up card ...")
card_url = f"{base_url}/cards/user/reference_app_user_1"
if not send_request("GET", card_url, headers):
    url = f"{base_url}/cards"
    post_data = {
        "token": "reference_app_card_1",
        "card_product_token": "reference_app_card_product_token",
        "user_token": "reference_app_user_1"
    }
    send_request("POST", url, headers, post_data)

print("Setting up webhook ...")
required_webhook_url = urljoin(ngrok_url, 'webhook')
url = f"{base_url}/webhooks/reference_app_webhook_token"
response = session.get(url, headers=headers, json={})
print( "GET  ", url, " ", response.status_code)
print(json.dumps(response.json(), indent=4))
if check_payload(response):
    response_data = response.json()["config"]
    if response_data["url"] != required_webhook_url or response_data[
        "basic_auth_username"] != ngrok_auth_username or response_data[
        "basic_auth_password"] != ngrok_auth_password:
        data = {
            "name": "Reference app webhook",
            "events": [
                "*"
            ],
            "config": {
                "url": required_webhook_url,
                "basic_auth_username": ngrok_auth_username,
                "basic_auth_password": ngrok_auth_password
            }
        }
        send_request("PUT", url, headers, data)
else:
    url = f"{base_url}/webhooks"
    data = {
        "token": "reference_app_webhook_token",
        "name": "Reference app webhook",
        "active": True,
        "events": [
            "*"
        ],
        "config": {
            "url": required_webhook_url,
            "basic_auth_username": ngrok_auth_username,
            "basic_auth_password": ngrok_auth_password
        }
    }
    send_request("POST", url, headers, data)
