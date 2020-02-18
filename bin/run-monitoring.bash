#!/usr/bin/env bash
set -euo pipefail

export LC_ALL=en_US.UTF-8


COMMAND=${1:-"help"}

__PWD=$PWD
__DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
__PARENT_DIR=$(dirname $__DIR)
__DIR_COMPOSE=$__PARENT_DIR/docker
__COMPOSE_FILE=$__DIR_COMPOSE/docker-compose.yml

#echo -e $__DIR
#echo -e $__PARENT_DIR
#echo -e $__COMPOSE_FILE
echo -e "\n\n$(<$__DIR/banner.txt)\n\n"

if [[ -f $__DIR/config.rc ]]; then

    echo -e "Loading config from $__DIR/config.rc"
    set -o allexport
    source $__DIR/config.rc
    set +o allexport
fi



#### Internal functions

make_status_requests () {
    echo -e "Checking components status:"
}


#### Main

if [[ $COMMAND == *"help"* ]]; then

    echo -e "Script for running the Keyko Web3 Monitoring solution using docker containers. This script runs:"
    echo -e "\t - Monitoring Agent"
    echo -e "\t - Kafka"
    echo -e "\t - Zookeeper"
    echo -e "\t - Schema Registry"
    echo -e "\t - Elastic Search"

    echo -e "Options:"
    echo -e "$0 <COMMAND>"
    echo -e "\t - Command; comma separated list of actions to execute. Options are: help, start, config, stop, status, reset"
    echo -e "\n"
    exit 0
fi

if [[ $COMMAND == *"pull"* ]]; then

    echo -e "* Downloading containers"
    docker-compose -f $__COMPOSE_FILE pull
fi

if [[ $COMMAND == *"start"* ]]; then

    echo -e "* Starting containers"
    docker-compose -f $__COMPOSE_FILE up
fi

if [[ $COMMAND == *"stop"* ]]; then

    echo -e "* Stopping containers"
    docker-compose -f $__COMPOSE_FILE down
fi

if [[ $COMMAND == *"status"* ]]; then

    docker ps

    echo -e "Schema Registry:"
    curl http://$REGISTRY_URL/config

    echo -e "Elastic Search:"
    curl -u $ELASTIC_USER:$ELASTIC_PASSWORD -XGET $ELASTIC_URL/_cluster/health?pretty

    echo -e "Kibana:"
    curl -I  $KIBANA_URL/status

    echo -e "Web3 Monitoring:"
    curl http://$AGENT_URL/monitoring/health
fi

if [[ $COMMAND == *"config"* ]]; then

    command -v newman > /dev/null 2>&1 || {
      echo >&2 "To configure the Monitoring Agent via REST requets it's necessary to have installed newman locally.\nPlease check the instruction options at: https://www.npmjs.com/package/newman\n\n";
      exit 1;
    }
    echo -e "* Configuring Monitoring Agent"
    newman run $__PARENT_DIR/docs/postman/agent-api.postman_collection.json

    echo -e "* Configuring Kafka Connect Driver"
    curl -X POST -H "Content-Type: application/json" --data \'$KAFKA_CONNECT_CONFIG\' http://localhost:8083/connectors

    echo -e "* Configuring Elastic Dynamic Template"
    echo curl -X PUT $ELASTIC_USER:$ELASTIC_PASSWORD@$ELASTIC_URL/_template/monitoring_dynamic_template -H 'Content-Type: application/json'  -d \'$ELASTIC_DYNAMIC_TEMPLATE\'

fi

if [[ $COMMAND == *"reset"* ]]; then
    docker rm -f web3-monitoring-agent kafka mongo zookeeper schema-registry || echo -e "Containers removed"
    echo -e "Cleaning database folders"
    sudo rm -rf $HOME/.mongodb/data/db
    sudo rm -rf $HOME/.elastic/data/db
fi

echo -e "\n"

