# Celo Web3 Monitoring 

Celo Web3 Monitoring provides a solution helping to analyze what is happening in the Celo Network.
It allows to ingest, transform & persist multiple sources of data facilitating further analysis and data consumption.

In the existing version the solution ingest the following data coming from a Celo network node:

    - Blocks minned
    - Events emitted from Celo Smart Contracts
    - State of public variables (views)

The solution is flexible to support the ingestion of additional sources of data (data from custodian services, API's, databases, websockets, etc.)
 and combine/enrich/analyze with other sources of data in real time.  

![High Level View](docs/images/high-level-view.png)

## Architecture

The solution architecture it's based in the following Open Source components:

- Keyko Web3 Agent - Ingestion agent able to connect a network node and ingest all the data related to blocks, events, transactions and public state. The agent is configurable via API, allowing to extend the elements to fetch from the network.
- Celo Events Streamer - Small processors in charge of data cleansing, cataloging and transformation facilitating further analysis and visualization. Based in Keyko event streamer framework.
- Data backbone -  Event driven data bus keeping all the incoming data and facilitating the event transformation and further persistence in real-time. Based in Kafka and Schema Registry of Confluent.  

For storage  and visualization purposes, the processed data is saved in Elastic Search. Dashboards facilitating the visualization are built using Kibana.

## How to run

### Components of the stack

You need to have running the following components:

- Kafka
- Zookeeper
- Schema Registry
- Web3 Monitoring Agent
- Elastic Search
    
You can have all of them running quickly with the docker-compose.yml in the folder `docker` and using next command:

```bash
docker-compose up
```

### How to run the Events Streamer

The rules implementing the Celo use cases are included as part of the Events Streamer. The code of this is included as part of this repository.
The easy way to compile and start running the events streamer is using the following commands:

```bash
mvn clean package
java -jar target/celo-monitoring.jar 
```

### Configure the Monitoring Agent

As was said above, the monitoring agent can ingest blocks, transactions, events and the state of public views. This configuration can be made easily via REST Api.
For loading the configuration allowing to ingest the information required for the Celo use cases, run the following commands:

```bash
# Install Newman
npm install -g newman

# Run the API agent requests
newman run docs/postman/agent-api.postman_collection.json 
```


## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

