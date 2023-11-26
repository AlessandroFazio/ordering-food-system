#!/bin/bash

set -xe

if [[ ! -d "$(pwd)/log" ]]; then
  mkdir -p "$(pwd)/log"
fi

# Redirect stdout and stderr to a file and also to the terminal
exec > >(tee log/bootstrap.log)
exec 2>&1

if [[ -f .env ]]; then
  source .env
else
  export GLOBAL_NETWORK=food-ordering-system
  export GROUP_ID=food.ordering.system
fi

if [[ ! -d "${DEBEZIUM_TARGET_LOCAL_DIR}" ]]; then
  echo "Creating ${DEBEZIUM_TARGET_LOCAL_DIR} for storing jar dependencies locally"
  mkdir -p "${DEBEZIUM_TARGET_LOCAL_DIR}"
  source "$(pwd)/scripts/utils/get-debezium-required-jars.sh"
fi

source "$(pwd)/scripts/utils/delete-local-volumes.sh"

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
  -f connect.yaml \
  up -d