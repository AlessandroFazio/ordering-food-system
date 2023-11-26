#/bin/bash

set -xe

cat >> ${PSQL_HBA_CONF_FILEPATH} <<EOF
local   replication     debezium                          trust
host    replication     debezium  127.0.0.1/32            trust
host    replication     debezium  ::1/128                 trust
EOF

cat >> ${PSQL_CONF_FILEPATH} <<EOF
#shared_preload_libraries = 'wal2json'
# REPLICATION
wal_level = logical
max_wal_senders = 4
# wal_keep_segments = 4
# wal_sender_timeout = 60s
max_replication_slots = 4
EOF

sed -i 's/^\(max_connections\s*=\s*\).*/\1200/' ${PSQL_CONF_FILEPATH}