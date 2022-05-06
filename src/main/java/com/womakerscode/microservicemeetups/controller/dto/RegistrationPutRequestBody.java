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
public class RegistrationPutRequestBody {

    @NotEmpty(message = "The name tag cannot be empty")
    private String nameTag;

}
