package com.womakerscode.microservicemeetups.controller.resource;

import com.womakerscode.microservicemeetups.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetups")
@RequiredArgsConstructor
public class MeetupController {

    //private final MeetupService meetupService;
    private final RegistrationService registrationService;
    private final ModelMapper modelMapper;

}
