package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import com.womakerscode.microservicemeetups.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationEventResponse {

    private Long id;

    private String title;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private String creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private String startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private String endDate;

    private EventTypeEnum eventTypeEnum;

    private Long organizerId;

}
