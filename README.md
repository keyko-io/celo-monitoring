# celo-monitoring
Celo Web3 Monitoring 

How to run
----------

You need to have running the following components:
    - kafka
    - zookeeper
    - schema-registry
    - web3-monitoring-agent
    
You can have all of them running quickly with the docker-compose.yml in the folder `docker` and using next command:
```bash
docker-compose up
```

The easy way to start is running the following commands:

```bash
mvn clean package
java -jar target/celo-monitoring.jar 
```
