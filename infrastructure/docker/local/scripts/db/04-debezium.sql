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

-- Create publication for outbox tables
CREATE PUBLICATION debezium_order_payment_outbox_pub FOR TABLE "order".payment_outbox;
CREATE PUBLICATION debezium_order_restaurant_approval_outbox_pub FOR TABLE "order".restaurant_approval_outbox;
CREATE PUBLICATION debezium_payment_order_outbox_pub FOR TABLE payment.order_outbox;
CREATE PUBLICATION debezium_restaurant_order_outbox_pub FOR TABLE restaurant.order_outbox;