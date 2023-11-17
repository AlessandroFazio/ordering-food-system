#!/bin/bash

set -xe

docker-compose \
  -f zookeeper.yaml \
  -f kafka.yaml \
  -f schema-registry.yaml \
  -f control-center.yaml \
  -f init_kafka.yaml \
  -f postgres.yaml \
  down