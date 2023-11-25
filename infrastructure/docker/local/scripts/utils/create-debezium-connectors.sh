#!/bin/sh

# Set the Kafka Connect node URL
KAFKA_CONNECT_URL="http://${KAFKA_CONNECT_HOSTPORT}/connectors"

# Make a POST request to create the connector on the specified node
for conn in $(ls ${KAFKA_CONNECT_CONNECTOR_CONFIG_DIR}/*.json); do
  echo "Creating connector: $(basename ${conn}) in kafka connect running at: ${KAFKA_CONNECT_HOSTPORT}"
  curl --silent -X POST -H "Content-Type: application/json" \
    --data "$(cat "${conn}")" "${KAFKA_CONNECT_URL}"
done