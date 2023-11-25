-- Create the role
CREATE ROLE customer_service LOGIN PASSWORD 'customer_password';

-- Drop the schema if it exists
DROP SCHEMA IF EXISTS customer CASCADE;

-- Create the schema
CREATE SCHEMA customer;

-- Grant privileges and ownership of schema to role
ALTER SCHEMA customer OWNER TO customer_service;
GRANT USAGE, CREATE ON SCHEMA customer TO customer_service;

-- Create the extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the table in the customer schema and set the owner
CREATE TABLE customer.customers
(
    id uuid NOT NULL,
    username character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

ALTER TABLE customer.customers OWNER TO customer_service;

-- Drop the materialized view if it exists
DROP MATERIALIZED VIEW IF EXISTS customer.order_customers_m_view;

-- Create the materialized view in the customer schema and set the owner
CREATE MATERIALIZED VIEW customer.order_customers_m_view
    TABLESPACE pg_default
AS
SELECT id,
       username,
       first_name,
       last_name
FROM customer.customers
WITH DATA;

ALTER MATERIALIZED VIEW customer.order_customers_m_view OWNER TO customer_service;

-- Drop function if it exists
DROP FUNCTION IF EXISTS customer.refresh_order_customers_m_view();

-- Create or replace the function in the customer schema and set the owner
CREATE OR REPLACE FUNCTION customer.refresh_order_customers_m_view()
    RETURNS TRIGGER
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW customer.order_customers_m_view;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

ALTER FUNCTION customer.refresh_order_customers_m_view() OWNER TO customer_service;

-- Create the trigger in the customer schema and set the owner
CREATE TRIGGER refresh_order_customers_m_view
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
    ON customer.customers
    FOR EACH STATEMENT
EXECUTE PROCEDURE customer.refresh_order_customers_m_view();

ALTER TABLE customer.customers OWNER TO customer_service;
