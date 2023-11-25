-- Create role for debezium
CREATE ROLE debezium REPLICATION LOGIN PASSWORD 'debezium_password';

-- Grant privileges to debezium user
GRANT CONNECT ON DATABASE postgres TO debezium;

-- Grant usage on schemas
GRANT USAGE ON SCHEMA "order" TO debezium;
GRANT USAGE ON SCHEMA payment TO debezium;
GRANT USAGE ON SCHEMA restaurant TO debezium;

-- Grant Select on outbox tables
GRANT SELECT ON TABLE "order".payment_outbox TO debezium;
GRANT SELECT ON TABLE "order".restaurant_approval_outbox TO debezium;
GRANT SELECT ON TABLE payment.order_outbox TO debezium;
GRANT SELECT ON TABLE restaurant.order_outbox TO debezium;

-- Create order outbox replication group
CREATE ROLE debezium_order_outbox_replication_group;
-- Grant the order outbox replication group role to debezium
GRANT debezium_order_outbox_replication_group TO debezium;
GRANT debezium_order_outbox_replication_group TO order_service;

-- Alter table ownership to the order outbox replication group
ALTER TABLE "order".payment_outbox OWNER TO debezium_order_outbox_replication_group;
ALTER TABLE "order".restaurant_approval_outbox OWNER TO debezium_order_outbox_replication_group;

-- Create payment outbox replication group
CREATE ROLE debezium_payment_outbox_replication_group;

-- Grant the payment outbox replication group role to debezium
GRANT debezium_payment_outbox_replication_group TO debezium;
GRANT debezium_payment_outbox_replication_group TO payment_service;

-- Alter table ownership to the payment outbox replication group
ALTER TABLE "payment".order_outbox OWNER TO debezium_payment_outbox_replication_group;

-- Create restaurant outbox replication group
CREATE ROLE debezium_restaurant_outbox_replication_group;

-- Alter table ownership to the restaurant outbox replication group
GRANT debezium_restaurant_outbox_replication_group TO debezium;
GRANT debezium_restaurant_outbox_replication_group TO restaurant_service;

-- Alter table ownership to the restaurant outbox replication group
ALTER TABLE restaurant.order_outbox OWNER TO debezium_restaurant_outbox_replication_group;

-- Create publication for outbox tables
CREATE PUBLICATION debezium_order_payment_outbox_pub FOR TABLE "order".payment_outbox;
CREATE PUBLICATION debezium_order_restaurant_approval_outbox_pub FOR TABLE "order".restaurant_approval_outbox;
CREATE PUBLICATION debezium_payment_order_outbox_pub FOR TABLE payment.order_outbox;
CREATE PUBLICATION debezium_restaurant_order_outbox_pub FOR TABLE restaurant.order_outbox;