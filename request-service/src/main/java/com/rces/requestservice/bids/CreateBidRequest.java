package com.rces.requestservice.bids;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateBidRequest(

        @Valid
        @NotEmpty(message = "BidItems не должен быть пустым!")
        List<BidItemRequest> items

) {

    public record BidItemRequest(

            @NotBlank(message = "ItemName обязателен")
            String itemName,

            @Min(value = 1, message = "Минимальное количество = 1")
            int quantity
    ) {
    }

}
