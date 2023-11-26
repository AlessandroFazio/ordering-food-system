package github.alessandrofazio.customer.service.messaging.mapper;

import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;
import github.alessandrofazio.kafka.order.avro.model.CustomerAvroModel;
import org.springframework.stereotype.Component;

@Component
public class CustomerMessagingDataMapper {
    public CustomerAvroModel customerCreatedEventToCustomerAvroModel(CustomerCreatedEvent customerCreatedEvent) {
        return CustomerAvroModel.newBuilder()
                .setId(customerCreatedEvent.getCustomerId().getValue())
                .setUsername(customerCreatedEvent.getUsername().getValue())
                .setFirstName(customerCreatedEvent.getCustomerInformation().getFirstName())
                .setLastName(customerCreatedEvent.getCustomerInformation().getLastName())
                .build();
    }
}
