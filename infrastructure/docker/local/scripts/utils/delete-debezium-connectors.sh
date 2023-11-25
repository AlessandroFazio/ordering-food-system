#!/bin/bash

set -xe

# Set the Kafka Connect node URL
KAFKA_CONNECT_URL="http://${KAFKA_CONNECT_HOSTPORT}/connectors"

response=$(curl --silent -X GET "${KAFKA_CONNECT_URL}")
connectors=$(echo "${response}" | jq -r '.[]')

if [ -z "${connectors}" ]; then
  echo "No connectors found. Terminating..."
  exit 0
fi

for conn in ${connectors}; do
  url="${KAFKA_CONNECT_URL}/${conn}"
  echo "Deleting connector: ${conn} at: ${url}"
  curl --silent -X DELETE ${url}
done