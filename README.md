# Synpulse8 Hiring Challenge: Backend Engineer Hiring Challenge

## Design
### Class Diagram
![](./diagrams/Class%20Diagram.png)

Each class also contains accessors and mutators for each class member.


### Sequence Diagram
![](./diagrams/Sequence%20Diagram.png)


## API Endpoint
Detailed API definitions can be found in openapi.yml

### Task-related
`GET /api/v1/transactions/get`: Return a paginated list of money account transactions 
created in an arbitrary calendar month for a given customer who is logged-on in the portal.

Request Header

| Parameter        | Type     | Description                                             |
|:-----------------|:---------|:--------------------------------------------------------|
| `Authorization` | `String` | Required, must start from `Bearer `, followed by a JWT. |

Query String

| Parameter       | Type     | Description                                    |
|:----------------|:---------|:-----------------------------------------------|
| `year`          | `int`    | Required, must be from range `2013` to `2022`. |
| `month`         | `int`    | Required, must be from range `1` to `12`.      |
| `page_size`     | `int`    | Optional, `100` by default.                    |
| `base_currency` | `String` | Optional, `USD` by default.                    |

Sample Response (JSON)
```
{
    "pages": [
        {
            "transactions": [
                {
                    "amount": -662820.0,
                    "currency": "USD",
                    "IBAN": "CH93-0000-0000-0000-0002-0",
                    "id": "00cfb677-afdb-4733-b692-b543fc52bb9f",
                    "date": "2018-12-20",
                    "description": "I lost -662820..."
                }
            ],
            "page_size": 1,
            "credit": 662820
        },
        {
            "transactions": [
                {
                    "amount": 494712.0,
                    "currency": "USD",
                    "IBAN": "CH93-0000-0000-0000-0002-0",
                    "id": "36fbf6f9-1b54-4faf-a8e8-703e043c81f7",
                    "date": "2018-12-06",
                    "description": "I earned 494712!"
                }
            ],
            "page_size": 1,
            "dedit": 494712
        }
    ],
    "num_pages": 2,
    "client_id": "P-0000000002"
}
```

Note that `pages > transactions > amount` is in quoted currency, while `pages > debit` and 
`pages > credit` is in base currency.


### Testing purpose
`GET /api/v1/admin/token/get`: Return the JWT for the given client, expired within 10 hours.

Query String

| Parameter   | Type     | Description                            |
|:------------|:---------|:---------------------------------------|
| `client_id` | `String` | Required. For example, `P-0000000002`. |

Sample Response (String)

`eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJQLTAwMDAwMDAwMDIiLCJleHAiOjE2NzUzNzI0MzksImlhdCI6MTY3NTMzNjQ
zOX0.htnYZ1ekZrusFxxg6gbV-xHxjX1ZFmHD8U8aO49HuTSJDVAagGnTkLqYQys316N4NEyfVSl5W6usY5zj97fKuQ`


`GET /api/v1/admin/transactions/publish`: Publish some account-related or 
transaction-related Kafka messages, stored in local files, to some Kafka topics.


## Security
For security, we assume each client is already authenticated and has a JWT at request header. 
In `SecurityConfig`, we restrict requests to all API endpoints, except `/api/v1/admin/**`, 
where these requests must be authorized by a custom web filter.

To retrieve the JWT, we defined a web filter `JWTAuthFilter` extended from 
`OncePerRequestFilter`. This filter will first extract the JWT substring from `Authorization` 
header. Then, it will query the user details by invoking `JWTUserDetailsService`. If 
the user details can be retrieved from the token, this indicates the JWT is issued for a 
'certified' client. 

Next, the filter will validate the token. If the token is valid, the 
filter will instantiate and set a `UsernamePasswordAuthenticationToken` for internal uses 
to indicate the user is authorized to retrieve transaction records. If any of the above steps 
goes wrong, the `UsernamePasswordAuthenticationToken` will not be instantiated and set. The 
request will not be authorized.

In `JWTUserDetailsService`, we only look up for the `client_id` in the JWT as the username. 
If the `client_id` exists in the record, we will return the corresponding user details. 
It is important to note that we use a constant variable of a list defined in 
`TransactionConstraints` to store the Client IDs we support. In production, we have to 
consider other alternatives, like using a database, for proper storage of Client IDs.

## Data
To retrieve transaction records for a given client, `TransactionService` will poll all 
`accounts information` of the given client from Kafka, then it will poll `transaction records` 
for each account one-by-one from Kafka.


### Data Generation
For testing purpose, a Python script (not included) is utilized to generate account information 
and transaction records for 10 clients. Each client contains 2 to 4 accounts in different 
currencies. For each account, 2,000 to 4,000 transaction records are generated for each month 
within 2013 to 2022.


### Account Information
The account information of all clients are stored as Kafka message under the Kafka topic 
`accounts`. For each Kafka message, the key is the `client_id`, and the value is an array 
of account objects. Each object contains 2 fields, `IBAN` and `currency`.


### Transaction Records
To ensure efficient data access from Kafka, multiple Kafka topics are defined. For each 
account, a Kafka topic is created for the transaction records for each month. Since the 
transaction records in the recent 10 years are stored, for each account, there are 
`10 years * 12 months = 120` Kafka topics created. Each topic is named as 
`transactions.<Account IBAN: String>.<year: int>.<month: int>`.

This allows us to poll the Kafka messages efficiently without processing transaction records 
in other months and without the need of filtering. This greatly reduces the time required 
for each request. To illustrate, if we created Kafka topics by account, without taking 
time into account, around 40 seconds are required to fetch all records. Yet, if we created 
Kafka topics by time and account, we only need around 5 seconds to fetch all records.

The downside of this approach is that increasing the number of Kafka topics in a broker will 
reduce the performance of the broker. Suppose we have a lot more clients and also accounts to 
store in the broker layer. To deal with this, by horizontal scaling (adding more servers as 
brokers), we can spread the Kafka topics over brokers and prevent performance degradation.


## Logging and Monitoring
To monitor the traffic around the API endpoint, a custom filter `RequestAndResponseLoggingFilter` 
adopted from [here](https://gist.github.com/michael-pratt/89eb8800be8ad47e79fe9edab8945c69) is 
defined to log all requests and response handled by the server. In addition, to monitor the 
flow of data, additional loggers are utilized among different services. Exceptions occurred 
in these services will also be logged to ensure an easier troubleshooting.


## Testing
Due to time limit, I didn't have enough time to develop the testing modules.