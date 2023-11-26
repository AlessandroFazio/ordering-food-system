package github.alessandrofazio.service.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.alessandrofazio.domain.valueobject.*;
import github.alessandrofazio.order.service.domain.entity.Customer;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.entity.Product;
import github.alessandrofazio.order.service.domain.entity.Restaurant;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.dto.create.CreateOrderCommand;
import github.alessandrofazio.service.domain.dto.create.CreateOrderResponse;
import github.alessandrofazio.service.domain.dto.create.OrderAddress;
import github.alessandrofazio.service.domain.dto.create.OrderItem;
import github.alessandrofazio.service.domain.mapper.OrderDataMapper;
import github.alessandrofazio.domain.event.payload.OrderPaymentEventPayload;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import github.alessandrofazio.service.domain.ports.input.service.OrderApplicationService;
import github.alessandrofazio.service.domain.ports.output.repository.CustomerRepository;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import github.alessandrofazio.service.domain.ports.output.repository.PaymentOutboxRepository;
import github.alessandrofazio.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static github.alessandrofazio.domain.constant.DomainConstants.UTC;
import static github.alessandrofazio.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand getCreateOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("a9e7c0f4-98e7-4a88-9e7b-11d2ec6e7cfb");
    private final UUID RESTAURANT_ID = UUID.fromString("5c49e6fb-2ab9-42a1-8d44-c2d7bc8f56f2");
    private final UUID PRODUCT_ID = UUID.fromString("d5b7e8f3-7db1-4e95-a7f1-8031db9a4e3c");
    private final UUID ORDER_ID = UUID.fromString("3a218f8d-458e-4c14-94c7-23ef4175b928");
    private final UUID SAGA_ID = UUID.fromString("3a218f8d-458e-4c14-94c7-23ef4175b943");
    private final BigDecimal PRICE = new BigDecimal("200.00");


    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .orderAddress(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(PRICE)
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                               OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(3)
                                       .price(new BigDecimal("50.00"))
                                       .subTotal(new BigDecimal("150.00"))
                                       .build()))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .orderAddress(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        getCreateOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .orderAddress(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        Customer customer = new Customer(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class))).thenReturn(getOrderPaymentOutboxMessage());
    }

    @Test
    public void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order created successfully", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException e = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals("Total price: 250.00 is not equal to Order items total: 200.00", e.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        OrderDomainException e =assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(getCreateOrderCommandWrongProductPrice));
        assertEquals(e.getMessage(), "Order item price: 60.00 is not valid for product " + PRODUCT_ID);
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        Restaurant passiveRestaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(passiveRestaurantResponse));

        OrderDomainException e = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand));

        assertEquals(e.getMessage(), "Restaurant with id " + RESTAURANT_ID + " is not currently active");
    }

    private OrderPaymentOutboxMessage getOrderPaymentOutboxMessage() {
        OrderPaymentEventPayload orderPaymentEventPayload = OrderPaymentEventPayload.builder()
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();

        return OrderPaymentOutboxMessage.builder()
                        .id(UUID.randomUUID())
                        .sagaId(SAGA_ID)
                        .createdAt(ZonedDateTime.now(ZoneId.of(UTC)))
                        .type(ORDER_SAGA_NAME)
                        .payload(createPayload(orderPaymentEventPayload))
                        .outboxStatus(OutboxStatus.STARTED)
                        .orderStatus(OrderStatus.PENDING)
                        .sagaStatus(SagaStatus.STARTED)
                        .outboxStatus(OutboxStatus.STARTED)
                        .version(0)
                .build();
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload);
        } catch (JsonProcessingException e) {
            throw new OrderDomainException("Cannot create OrderPaymentEventPayload object");
        }
    }
}
