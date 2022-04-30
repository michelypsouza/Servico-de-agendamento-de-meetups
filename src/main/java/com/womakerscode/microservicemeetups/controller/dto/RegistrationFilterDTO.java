package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.womakerscode.microservicemeetups.util.DateUtil;

import java.util.Date;

public class RegistrationFilterDTO {

    private Long id;

    private String nameTag;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_TIME_PATTERN_DEFAULT)
    private Date dateOfRegistration;

    private Long eventId;

    private String eventTitle;

    private Long participantId;

}
