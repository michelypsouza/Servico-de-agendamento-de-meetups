package com.womakerscode.microservicemeetups.model.enumeration;

import lombok.Getter;

public enum EventTypeEnum {

    FACE_TO_FACE(1,"face-to-face"),
    ONLINE(2,"on-line");


    @Getter private Integer code;
    @Getter private String description;

    EventTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
