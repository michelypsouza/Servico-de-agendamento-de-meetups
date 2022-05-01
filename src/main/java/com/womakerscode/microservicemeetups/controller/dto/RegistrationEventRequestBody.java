package com.womakerscode.microservicemeetups.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationEventRequestBody {

    @NotNull(message = "The id of the event cannot be empty")
    private Long id;

    //private Long organizerId;

}
