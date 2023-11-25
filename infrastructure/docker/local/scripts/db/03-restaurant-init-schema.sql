CREATE ROLE restaurant_service LOGIN PASSWORD 'restaurant_password';

DROP SCHEMA IF EXISTS restaurant CASCADE;

CREATE SCHEMA restaurant;

-- Grant privileges and ownership of schema to role
ALTER SCHEMA restaurant OWNER TO restaurant_service;
GRANT USAGE, CREATE ON SCHEMA restaurant TO restaurant_service;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS restaurant.restaurants CASCADE;

CREATE TABLE restaurant.restaurants
(
    id uuid NOT NULL,
    name character varying COLLATE  pg_catalog."default" NOT NULL,
    active boolean NOT NULL,
    CONSTRAINT restaurants_pkey PRIMARY KEY (id)
);

ALTER TABLE restaurant.restaurants OWNER TO restaurant_service;

DROP TYPE IF EXISTS restaurant.approval_status;

CREATE TYPE restaurant.approval_status AS ENUM('APPROVED', 'REJECTED');

DROP TABLE IF EXISTS restaurant.order_approval CASCADE;

CREATE TABLE restaurant.order_approval
(
    id uuid NOT NULL,
    restaurant_id uuid NOT NULL,
    order_id uuid NOT NULL,
    status restaurant.approval_status NOT NULL,
    CONSTRAINT order_approval_pkey PRIMARY KEY (id)
);

ALTER TABLE restaurant.order_approval OWNER TO restaurant_service;

DROP TABLE IF EXISTS restaurant.products CASCADE;

CREATE TABLE restaurant.products
(
    id uuid NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    price numeric(10,2) NOT NULL,
    available boolean NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

ALTER TABLE restaurant.products OWNER TO restaurant_service;

DROP TABLE IF EXISTS restaurant.restaurant_products CASCADE;

CREATE TABLE restaurant.restaurant_products
(
    id uuid NOT NULL,
    restaurant_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT restaurant_products_pkey PRIMARY KEY (id)
);

ALTER TABLE restaurant.restaurant_products OWNER TO restaurant_service;

ALTER TABLE restaurant.restaurant_products
    ADD CONSTRAINT "FK_RESTAURANT_ID" FOREIGN KEY (restaurant_id)
    REFERENCES restaurant.restaurants (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT
    NOT VALID;

ALTER TABLE restaurant.restaurant_products
    ADD CONSTRAINT "FK_PRODUCT_ID" FOREIGN KEY (product_id)
        REFERENCES restaurant.products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT
        NOT VALID;

DROP MATERIALIZED VIEW IF EXISTS restaurant.order_restaurant_m_view;

CREATE MATERIALIZED VIEW restaurant.order_restaurant_m_view
TABLESPACE pg_default
AS
    SELECT r.id AS restaurant_id,
           r.name AS restaurant_name,
           r.active AS restaurant_active,
           p.id AS product_id,
           p.name AS product_name,
           p.price AS product_price,
           p.available AS product_available
        FROM restaurant.restaurants as r,
             restaurant.products as p,
             restaurant.restaurant_products as rp
    WHERE r.id = rp.restaurant_id AND p.id = rp.product_id
WITH DATA;

ALTER MATERIALIZED VIEW restaurant.order_restaurant_m_view OWNER TO restaurant_service;

refresh materialized VIEW restaurant.order_restaurant_m_view;

DROP function IF EXISTS restaurant.refresh_order_restaurant_m_view;

CREATE OR replace function restaurant.refresh_order_restaurant_m_view()
returns trigger
AS '
BEGIN
    refresh materialized VIEW restaurant.order_restaurant_m_view;
    return null;
END;
' LANGUAGE plpgsql;

ALTER FUNCTION restaurant.refresh_order_restaurant_m_view() OWNER TO restaurant_service;

DROP trigger IF EXISTS refresh_order_restaurant_m_view ON restaurant.restaurant_products;

CREATE trigger refresh_order_restaurant_m_view
after INSERT OR UPDATE OR DELETE OR truncate
ON restaurant.restaurant_products FOR each statement
EXECUTE PROCEDURE restaurant.refresh_order_restaurant_m_view();

DROP TYPE IF EXISTS restaurant.outbox_status;

CREATE TYPE restaurant.outbox_status AS ENUM('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS restaurant.order_outbox CASCADE;

CREATE TABLE restaurant.order_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status restaurant.outbox_status NOT NULL,
    order_approval_status restaurant.approval_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT order_outbox_pkey PRIMARY KEY (id)
);

ALTER TABLE restaurant.order_outbox OWNER TO restaurant_service;

CREATE INDEX "restaurant_order_outbox_saga_status"
    ON restaurant.order_outbox
        (type, order_approval_status);

CREATE UNIQUE INDEX "restaurant_order_outbox_saga_id"
    ON restaurant.order_outbox
        (type, saga_id, order_approval_status, outbox_status);