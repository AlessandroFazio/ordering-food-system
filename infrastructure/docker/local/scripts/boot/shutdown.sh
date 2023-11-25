#!/bin/bash

set -xe

# Redirect stdout and stderr to a file and also to the terminal
exec > >(tee log/shutdown.log)
exec 2>&1

docker-compose \
  -f zookeeper.yaml \
  -f kafka.yaml \
  -f schema-registry.yaml \
  -f control-center.yaml \
  -f init_kafka.yaml \
  -f postgres.yaml \
  -f connect.yaml \
  down

source "$(pwd)/scripts/utils/delete-local-volumes.sh"