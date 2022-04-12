package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.womakerscode.microservicemeetups.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTO {

    private Integer id;

    @NotEmpty(message = "The name cannot be empty")
    private String name;

    @NotEmpty(message = "The date of registration cannot be empty")
    @JsonFormat(pattern = DateUtil.DATE_PATTERN_DEFAULT)
    //@Pattern(regexp = DateUtil.DATE_PATTERN_DEFAULT)
    private String dateOfRegistration;

    @NotEmpty
    private String registration;

}
