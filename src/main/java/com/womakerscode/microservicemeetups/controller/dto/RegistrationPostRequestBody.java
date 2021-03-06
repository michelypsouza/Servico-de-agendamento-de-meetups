package com.womakerscode.microservicemeetups.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationPostRequestBody {

    @NotNull(message = "The event cannot be empty")
    private Long eventId;

    @NotEmpty(message = "The name tag cannot be empty")
    private String nameTag;

    @NotNull(message = "The event cannot be empty")
    private Long participantId;

}
