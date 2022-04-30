package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import com.womakerscode.microservicemeetups.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPostRequestBody {

    @NotEmpty(message = "The title of the event cannot be empty")
    private String title;

    @NotEmpty(message = "The description of the event cannot be empty")
    private String description;

    @NotNull(message = "The date of start of the event cannot be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private String startDate;

    @NotNull(message = "The date of end of the event cannot be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private String endDate;

    @NotNull(message = "The type of event cannot be empty")
    private EventTypeEnum eventTypeEnum;

    @NotNull(message = "The organizer cannot be empty")
    private Long organizerId;

}
