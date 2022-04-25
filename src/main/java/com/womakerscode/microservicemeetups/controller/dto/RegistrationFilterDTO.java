package com.womakerscode.microservicemeetups.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.womakerscode.microservicemeetups.util.DateUtil;

import java.util.Date;

public class RegistrationFilterDTO {

    private Integer id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.DATE_PATTERN_DEFAULT)
    @JsonSerialize(as = Date.class)
    private Date dateOfRegistration;

    private String registration;

}
