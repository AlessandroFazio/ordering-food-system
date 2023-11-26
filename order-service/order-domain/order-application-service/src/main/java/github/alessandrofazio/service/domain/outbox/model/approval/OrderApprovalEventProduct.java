package github.alessandrofazio.service.domain.outbox.model.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.stream.IntStream;

@Getter
@Builder
@AllArgsConstructor
public class OrderApprovalEventProduct {

    @JsonProperty
    private String id;
    @JsonProperty
    private Integer quantity;
}
