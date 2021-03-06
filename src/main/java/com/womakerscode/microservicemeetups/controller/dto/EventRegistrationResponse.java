package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.womakerscode.microservicemeetups.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRegistrationResponse {

    private Long id;

    @NotEmpty(message = "The name tag cannot be empty")
    private String nameTag;

    @NotNull(message = "The date of registration cannot be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private String dateOfRegistration;

    @NotNull(message = "The event cannot be empty")
    private Long participantId;

}
