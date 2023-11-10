package github.alessandrofazio.service.domain.ports.input.service;

import github.alessandrofazio.service.domain.dto.create.CreateOrderCommand;
import github.alessandrofazio.service.domain.dto.create.CreateOrderResponse;
import github.alessandrofazio.service.domain.dto.track.TrackOrderResponse;
import github.alessandrofazio.service.domain.dto.track.TrackerOrderQuery;
import jakarta.validation.Valid;

public interface OrderApplicationService {
    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);
    TrackOrderResponse trackOrder(@Valid TrackerOrderQuery trackerOrderQuery);
}
