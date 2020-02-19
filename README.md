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
    
You can have all of them running with the docker-compose.yml in the folder `docker` and using next command:

```bash
bin/run-monitoring.bash start
```

Starting the all the components will take a few minutes, you can check if everything is up running:
```bash
bin/run-monitoring.bash status
```

### How to run the Monitoring Agent

When all the components look okay, you can proceed to start the [Web3 Monitoring Agent](https://github.com/keyko-io/web3-monitoring-agent). 
You can use the Docker image or compiling/running directly the application:

```bash
cd /path/to/web3-monitoring-agent
mvn clean package
ETHEREUM_NODE_URL=http://localhost:8545 java -jar server/target/web3-monitoring-agent-*.jar --spring.config.location=file:server/src/main/resources/application.yml
```

At this point without further configuration, the agent is ingesting blocks and sending to Kafka.
You can check the agent is sending the blocks listening directly from the Kafka topic:

```bash
bin/run-monitoring.bash read-topic w3m-block-events
```

Each 5 seconds approximately you should see a new line representing a block ingested in the system.

### Configuring the Agent & other components

As was said above, the monitoring agent can ingest blocks, transactions, events and the state of public views. This configuration can be made easily via REST Api.
For loading the configuration allowing to ingest the information required for the Celo use cases, run the following commands:

```bash
# Install Newman
npm install -g newman

# Run the API agent requests
bin/run-monitoring.bash config 
```

The above step is also in adding the Kafka Connect & Elastic Search configuration to move the output data from Kafka to Elastic.

You can check the agent is reading the information when the configured events are triggered directly from the Kafka topic:

```bash
bin/run-monitoring.bash read-topic w3m-contract-events
```

You can check the agent is reading the information pulled from public views listening directly from the Kafka topic:

```bash
bin/run-monitoring.bash read-topic w3m-contract-views
```

The rules for ingesting public views have different block intervals (between 5 and 25 blocks), so it could take a couple of minutes till you see some data comming to Kafka.


### How to run the Processing Rules

The rules implementing the Celo use cases are included as part of this repository. 
The processing engine uses the Open Source [Keyko Web3 Events Streamer framework](https://github.com/keyko-io/web3-event-streamer/). 
The easy way to compile and start running the processing is using the following commands:

```bash
mvn clean package
java -jar target/celo-monitoring-engine.jar 
```

### Checking that everything is running




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

