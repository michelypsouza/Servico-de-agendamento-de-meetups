package com.womakerscode.microservicemeetups.service.impl;

import com.womakerscode.microservicemeetups.controller.dto.MeetupFilterDTO;
import com.womakerscode.microservicemeetups.model.entity.Meetup;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.repository.MeetupRepository;
import com.womakerscode.microservicemeetups.service.MeetupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeetupServiceImpl implements MeetupService {

    private MeetupRepository meetupRepository;

    public MeetupServiceImpl(MeetupRepository meetupRepository) {
        this.meetupRepository = meetupRepository;
    }

    @Override
    public Meetup save(Meetup meetup) {
        return meetupRepository.save(meetup);
    }

    @Override
    public Optional<Meetup> getById(Integer id) {
        return meetupRepository.findById(id);
    }

    @Override
    public Meetup update(Meetup meetup) {
        return meetupRepository.save(meetup);
    }

    @Override
    public Page<Meetup> find(MeetupFilterDTO filterDTO, Pageable pageable) {
        return meetupRepository.findByRegistrationOnMeetup( filterDTO.getRegistration(), filterDTO.getEvent(), pageable );
    }


    @Override
    public Page<Meetup> getRegistrationsByMeetup(Registration registration, Pageable pageable) {
        return meetupRepository.findByRegistration(registration, pageable);
    }

}
