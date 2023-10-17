# JIT Reference App
This reference app is a sample webserver that demonstrates the implementation of the two endpoints required for integrating with Marqeta's [Just-in-Time Funding](https://www.marqeta.com/docs/developer-guides/about-jit-funding) feature:
* /gateway - receives and responds to Funding Requests
* /webhook - receives and process Funding Notifications 

The scenarios folder contain a subset of real world transaction flows simulated using [Simulations 2.0](https://www.marqeta.com/docs/core-api/simulations-card-transactions) that your JIT implementation must handle.

Transaction flows showcased are:
*  Basic authorization followed by clearing transaction.
*  Authorization and clearing transaction flow with multiple clearing transaction correspond to the same authorization.
*  Authorization and clearing transaction flow with clearing amount greater than authorization amount.
*  Clearing only transactions demonstrating force post.
*  Authorization --> advice -- > clearing flow.
*  Authorization --> incremental --> clearing flow.
*  Handling error condition with a simulated a delayed funding response
*  Declining a funding authorization request.
*  Basic PIN debit scenario showcasing single message transaction. 
*  Transaction flows involving reversal
*  Basic refund transaction scenario


## Terms of use
By using this reference app, you agree to [Marqeta's API terms of use](https://www.marqeta.com/api-terms).

## Setup
This project uses [ngrok](https://ngrok.com/docs) to expose your local reference app endpoints to the internet.
Install ngrok using [these instructions](https://ngrok.com/docs/getting-started/).

## Dependencies
* Java 17 with [Gradle](https://gradle.org)
* [Docker](https://www.docker.com/products/docker-desktop/)
* Python 2.x/ 3.x with [pip](https://pypi.org/project/pip/) [installed](https://pip.pypa.io/en/stable/installation/)
* Python requests [installed](https://pypi.org/project/requests/) 
    ```bash
    python -m pip install requests
    ```
## Build
From the project root directory, execute:
```bash
gradle clean build
./gradlew bootJar --info
docker build -t referenceapp .
```
## Run
```bash
docker run -p 8080:8080 referenceapp
```

## Expose endpoints using ngrok
The exposed endpoints in ngrok must support basic auth with a minimum 20-50 character password. The password must have
* Must contain at least one numeral
* Must contain at least one lowercase letter
* Must contain at least one uppercase letter
* Must contain at least one of these symbols: @ # $ % ! ^ & * ( ) \ _ + ~ ` - = [ ] { } , ; : ' " , . / < > ?
* 
```bash
export NGROK_AUTH_USERNAME=<username>
export NGROK_AUTH_PASSWORD=<20-50 character password>
ngrok http 8080 --basic-auth=$NGROK_AUTH_USERNAME:$NGROK_AUTH_PASSWORD
```
## Configure your sandbox

### Step 1
Your public sandbox credentials are available at https://app.marqeta.com/development 
or if using your private sandbox by your Marqeta representative.
```bash
export SANDBOX_URL = "https://sandbox-api.marqeta.com"
export APPLICATION_TOKEN =<application token>
export ACCESS_TOKEN = <access token>
export NGROK_URL = <https://....ngrok-free.app>
```
If you are a managed JIT customer, you must also execute:
```bash
export MANAGED_JIT = true
```

### Step 2
Run the python setup script to configure your private sandbox with all the resources required by the app.
The setup script creates a card product, user, card, webhook, and gateway (if applicable).
```bash
cd scripts/configure
python setup.py
```
Re-run the setup script every time any of your environment values (such as the NGROK_URL) changes.
You can skip this step if none of your environment variable values have changed.

## Run Scenarios
After you have configured your sandbox, you can run the transaction scenarios.
```bash
cd scripts/scenarios
python scenarioselector.py
```
The console output from Docker shows all the messages received from Marqeta and also helpful [ledger management](https://www.marqeta.com/docs/developer-guides/ledger-management-with-jit-funding) pointers for the simulated events.
