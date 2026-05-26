package com.laundry.system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacementDto {

    @NotNull(message = "Pickup date and time is required")
    private String pickupDate; // ISO String from datetime-local input

    @NotEmpty(message = "Your order must contain at least one laundry item")
    @Valid
    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();

    @Builder.Default
    private Double discount = 0.0;
}
