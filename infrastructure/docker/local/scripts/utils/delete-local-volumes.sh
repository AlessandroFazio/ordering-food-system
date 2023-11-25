#!/bin/bash

set -xe

volume_names=(
  "local_food-ordering-kafka1"
  "local_food-ordering-kafka2"
  "local_food-ordering-kafka3"
  "local_food-ordering-zookeeper-data"
  "local_food-ordering-zookeeper-transactions"
  "local_food_ordering_db"
)

for vol in "${volume_names[@]}"; do
  if docker volume inspect "${vol}" &> /dev/null; then
    echo "Deleting existent docker volume: ${vol}"
    docker volume rm "${vol}"
  else
    echo "Volume does not exist: ${vol}"
  fi
done