#!/bin/bash

set -xe

MAVEN_CENTRAL_REPO_BASE_URL="https://repo1.maven.org/maven2"
CONFLUENT_REPO_BASE_URL="https://packages.confluent.io/maven/io/confluent"

URLS=(
  "${MAVEN_CENTRAL_REPO_BASE_URL}/org/apache/avro/avro/${AVRO_VERSION}/avro-${AVRO_VERSION}.jar"
  "${MAVEN_CENTRAL_REPO_BASE_URL}/com/google/guava/guava/${GUAVA_VERSION}/guava-${GUAVA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/common-utils/${KAFKA_VERSION}/common-utils-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/common-config/${KAFKA_VERSION}/common-config-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-avro-serializer/${KAFKA_VERSION}/kafka-avro-serializer-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-connect-avro-converter/${KAFKA_VERSION}/kafka-connect-avro-converter-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-connect-avro-data/${KAFKA_VERSION}/kafka-connect-avro-data-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-schema-converter/${KAFKA_VERSION}/kafka-schema-converter-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-schema-registry/${KAFKA_VERSION}/kafka-schema-registry-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-schema-registry-client/${KAFKA_VERSION}/kafka-schema-registry-client-${KAFKA_VERSION}.jar"
  "${CONFLUENT_REPO_BASE_URL}/kafka-schema-serializer/${KAFKA_VERSION}/kafka-schema-serializer-${KAFKA_VERSION}.jar"
)

for url in ${URLS[@]}; do
  echo "Downloading artifact from ${url} to ${DEBEZIUM_TARGET_LOCAL_DIR}"
  wget "${url}" -P "${DEBEZIUM_TARGET_LOCAL_DIR}"
done


