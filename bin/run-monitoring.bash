#!/usr/bin/env bash
set -euo pipefail

export LC_ALL=en_US.UTF-8


COMMAND=${1:-"help"}

__PWD=$PWD
__DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
__PARENT_DIR=$(dirname $__DIR)
__CONF_DIR=$__PARENT_DIR/config
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
    echo -e "\t - Command; comma separated list of actions to execute. Options are: help, start, config-agent, config, stop, status, reset, list-topics, read-topic"
    echo -e "\n"
    exit 0
fi

if [[ $COMMAND == *"list-topics"* ]]; then

    echo -e "* Listing Topics:\n"
    docker exec -it kafka kafka-topics --bootstrap-server kafka:29092 --list
fi


if [[ $COMMAND == *"read-topic"* ]]; then

    OPTIONS=${3:-""}
    echo -e "* Reading from Avro Topic:\n"
    docker exec -it schema-registry kafka-avro-console-consumer --bootstrap-server kafka:29092 --topic $2 $OPTIONS
fi


if [[ $COMMAND == *"pull"* ]]; then

    echo -e "* Downloading containers"
    docker-compose -f $__COMPOSE_FILE pull
fi

if [[ $COMMAND == *"start"* ]]; then

    echo -e "* Starting containers"
    docker-compose -f $__COMPOSE_FILE up -d
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


if [[ $COMMAND == *"config-agent"* ]]; then

    command -v newman > /dev/null 2>&1 || {
      echo >&2 "To configure the Monitoring Agent via REST requets it's necessary to have installed newman locally.\nPlease check the instruction options at: https://www.npmjs.com/package/newman\n\n";
      exit 1;
    }
    echo -e "\n\n* Configuring Monitoring Agent\n"
    newman run $__PARENT_DIR/docs/postman/agent-api.postman_collection.json

fi

if [[ $COMMAND == *"config"* ]]; then
#    set -x

    echo -e "\n\n* Configuring Elastic Dynamic Template\n"
    curl -X PUT -u $ELASTIC_USER:$ELASTIC_PASSWORD -H "Content-Type: application/json"  --data @$__DIR/http-request-elastic-template.txt http://$ELASTIC_URL/_template/monitoring_dynamic_template

    echo -e "\n\n* Configuring Kafka Connect Driver\n"

    curl -X POST -H "Content-Type: application/json" --data @$__CONF_DIR/connect/connect-config.txt http://$CONNET_URL/connectors
    curl -X POST -H "Content-Type: application/json" --data @$__CONF_DIR/connect/contract-blocks-connector.txt http://$CONNET_URL/connectors
    curl -X POST -H "Content-Type: application/json" --data @$__CONF_DIR/connect/contract-events-connector.txt http://$CONNET_URL/connectors
    curl -X POST -H "Content-Type: application/json" --data @$__CONF_DIR/connect/contract-views-connector.txt http://$CONNET_URL/connectors

    echo -e "\n\n* Configuring Kibana Dashboard\n"
    curl -X POST -u $ELASTIC_USER:$ELASTIC_PASSWORD "$KIBANA_URL/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@$__CONF_DIR/kibana/pos-dashboard.ndjson
    curl -X POST -u $ELASTIC_USER:$ELASTIC_PASSWORD "$KIBANA_URL/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@$__CONF_DIR/kibana/monitoring-dashboard.ndjson
    curl -X POST -u $ELASTIC_USER:$ELASTIC_PASSWORD "$KIBANA_URL/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@$__CONF_DIR/kibana/stability-dashboard.ndjson

fi

if [[ $COMMAND == *"reset"* ]]; then
    docker rm -f web3-monitoring-agent kafka mongo zookeeper schema-registry elasticsearch kibana || echo -e "Containers removed"
    echo -e "Cleaning database folders"
    sudo rm -rf $HOME/.mongodb/data/db
    sudo rm -rf $HOME/.elastic/data/db
fi

echo -e "\n"

