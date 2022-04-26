package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.controller.dto.MeetupFilterDTO;
import com.womakerscode.microservicemeetups.model.entity.Meetup;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Optional<Meetup> getById(Integer id);

    Meetup update(Meetup meetup);

    Page<Meetup> find(MeetupFilterDTO filterDTO, Pageable pageable);

    Page<Meetup> getRegistrationsByMeetup(Registration registration, Pageable pageable);

}
