package com.rces.requestservice.bids;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateBidStatus(

        @NotNull(message = "Заявка должна быть обязательно указана!")
        Long bidId,

        @NotBlank(message = "Статус заявки должен быть указан!")
        BidStatus status,

        LocalTime timer
) {

}
