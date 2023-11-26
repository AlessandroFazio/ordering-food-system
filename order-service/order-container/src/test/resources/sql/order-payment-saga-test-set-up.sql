INSERT INTO "order".orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
VALUES('d215b5f8-0249-4dc5-89a3-51fd148cfb17', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41',
       'd215b5f8-0249-4dc5-89a3-51fd148cfb45', 'd215b5f8-0249-4dc5-89a3-51fd148cfb18', 100.00, 'PENDING', '');

INSERT INTO "order".order_items(id, order_id, product_id, price, quantity, sub_total)
VALUES(1, 'd215b5f8-0249-4dc5-89a3-51fd148cfb17', 'd215b5f8-0249-4dc5-89a3-51fd148cfb47', 100.00, 1, 100.00);

INSERT INTO "order".order_address(id, order_id, street, postal_code, city)
VALUES('d215b5f8-0249-4dc5-89a3-51fd148cfb15', 'd215b5f8-0249-4dc5-89a3-51fd148cfb17', 'test street', '1000AA', 'test city');

INSERT INTO "order".payment_outbox(id, saga_id, created_at, type, payload, outbox_status, saga_status, order_status, version)
VALUES('d215b5f8-0249-4dc5-89a3-51fd148cfbf4', 'd215b5f8-0249-4dc5-89a3-51fd148cfafa', current_timestamp, 'OrderProcessingSaga',
       '{"price": 100, "orderId": "d215b5f8-0249-4dc5-89a3-51fd148cfba5", "createdAt": "2022-01-07t16:21:42.917756+01:00", ' ||
       '"customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41", "paymentOrderStatus": "PENDING"}',
       'STARTED', 'STARTED', 'PENDING', 0);

