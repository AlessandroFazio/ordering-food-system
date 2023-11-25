#!/bin/bash

set -xe

psql -h localhost -U postgres -d postgres <<-EOSQL

--Create new root user for food ordering system database
CREATE USER food_ordering_system_root WITH PASSWORD 'root';

--Create new food ordering system database
DROP DATABASE IF EXISTS ${ORDERING_FOOD_SYSTEM_DB} CASCADE;
CREATE DATABASE ${ORDERING_FOOD_SYSTEM_DB} WITH OWNER = food_ordering_system_root;

--Grant all privileges to the new database
GRANT ALL PRIVILEGES ON DATABASE ${ORDERING_FOOD_SYSTEM_DB} TO food_ordering_system_root;
ALTER USER food_ordering_system_root CREATEROLE;
EOSQL