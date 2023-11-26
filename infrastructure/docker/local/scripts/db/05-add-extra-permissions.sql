-- Grant usage on schema restaurant to order_service for restaurant information look up
GRANT USAGE ON SCHEMA restaurant TO order_service;

-- Grant SELECT on order m_view for restaurant information look up by order_service
GRANT SELECT ON restaurant.order_restaurant_m_view TO order_service;
