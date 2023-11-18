#!/bin/bash

set -xe

if [[ -f .env ]]; then
  source .env
else
  export GLOBAL_NETWORK=food-ordering-system
  export GROUP_ID=food.ordering.system
fi

source "$(pwd)/scripts/delete-local-volumes.sh"

if ! docker network inspect ${GLOBAL_NETWORK} &> /dev/null; then
    echo "Creating Global Network: ${GLOBAL_NETWORK}"
    docker network create --driver bridge "${GLOBAL_NETWORK}";
else
    echo "Global Network: ${GLOBAL_NETWORK} already exists"
fi

docker-compose \
  -f zookeeper.yaml \
  -f kafka.yaml \
  -f schema-registry.yaml \
  -f control-center.yaml \
  -f init_kafka.yaml \
  -f postgres.yaml \
  up -d