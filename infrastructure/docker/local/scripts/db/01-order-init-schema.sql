-- Create the role
CREATE ROLE order_service LOGIN PASSWORD 'order_password';

DROP SCHEMA IF EXISTS "order" CASCADE;

CREATE SCHEMA "order";

-- Grant privileges and ownership of schema to role
ALTER SCHEMA "order" OWNER TO order_service;
GRANT USAGE, CREATE ON SCHEMA "order" TO order_service;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS "order".order_status;
CREATE TYPE "order".order_status AS ENUM ('PENDING', 'PAID', 'APPROVED', 'CANCELLED', 'CANCELLING');

DROP TABLE IF EXISTS "order".orders CASCADE;

CREATE TABLE "order".orders
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    restaurant_id uuid NOT NULL,
    tracking_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    order_status "order".order_status NOT NULL,
    failure_messages character varying COLLATE pg_catalog."default",
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

ALTER TABLE "order".orders OWNER TO order_service;

DROP TABLE IF EXISTS "order".order_items CASCADE;

CREATE TABLE "order".order_items
(
    id bigint NOT NULL,
    order_id uuid NOT NULL,
    product_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    quantity integer NOT NULL,
    sub_total numeric(10,2) NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id, order_id)
);

ALTER TABLE "order".order_items OWNER TO order_service;

ALTER TABLE "order".order_items
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
    REFERENCES "order".orders (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    NOT VALID;

DROP TABLE IF EXISTS "order".order_address CASCADE;

CREATE TABLE "order".order_address
(
    id uuid NOT NULL,
    order_id uuid NOT NULL,
    street character varying COLLATE pg_catalog."default" NOT NULL,
    postal_code character varying COLLATE pg_catalog."default" NOT NULL,
    city character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT order_address_pkey PRIMARY KEY (id, order_id)
);

ALTER TABLE "order".order_address OWNER TO order_service;

ALTER TABLE "order".order_address
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;

DROP TYPE IF EXISTS "order".saga_status;
CREATE TYPE "order".saga_status AS ENUM('STARTED', 'FAILED', 'SUCCEEDED', 'PROCESSING', 'COMPENSATING', 'COMPENSATED');

DROP TYPE IF EXISTS "order".outbox_status;
CREATE TYPE "order".outbox_status AS ENUM('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS "order".payment_outbox CASCADE;

CREATE TABLE "order".payment_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status "order".outbox_status NOT NULL,
    saga_status "order".saga_status NOT NULL,
    order_status "order".order_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT payment_outbox_pkey PRIMARY KEY (id)
);

ALTER TABLE "order".payment_outbox OWNER TO order_service;

CREATE INDEX "payment_outbox_saga_status"
    ON "order".payment_outbox
    (type, outbox_status, saga_status);

CREATE UNIQUE INDEX "payment_outbox_saga_id"
    ON "order".payment_outbox
    (type, saga_id, saga_status);

CREATE TABLE "order".restaurant_approval_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status "order".outbox_status NOT NULL,
    saga_status "order".saga_status NOT NULL,
    order_status "order".order_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT restaurant_approval_outbox_pkey PRIMARY KEY (id)
);

ALTER TABLE "order".restaurant_approval_outbox OWNER TO order_service;

CREATE INDEX "restaurant_approval_outbox_saga_status"
    ON "order".restaurant_approval_outbox
        (type, outbox_status, saga_status);

CREATE UNIQUE INDEX "restaurant_approval_outbox_saga_id"
    ON "order".restaurant_approval_outbox
        (type, saga_id, saga_status);

DROP TABLE IF EXISTS "order".customers CASCADE;

CREATE TABLE "order".customers
(
    id uuid NOT NULL,
    username character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

ALTER TABLE "order".customers OWNER TO order_service;