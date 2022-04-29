package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.womakerscode.microservicemeetups.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPutRequestBody {

    @NotEmpty(message = "The title of the event cannot be empty")
    private String title;

    @NotEmpty(message = "The description of the event cannot be empty")
    private String description;

    @NotNull(message = "The date of start of the event cannot be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private LocalDateTime startDate;

    @NotNull(message = "The date of end of the event cannot be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private LocalDateTime endDate;

    //private List<RegistrationDTO> registrations;

}
