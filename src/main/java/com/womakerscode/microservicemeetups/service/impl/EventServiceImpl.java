package com.womakerscode.microservicemeetups.service.impl;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.repository.EventRepository;
import com.womakerscode.microservicemeetups.service.EventService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event save(Event event) {
        if (existsByEvent(event)) {
            throw new BusinessException("Event already created");
        }
        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> getById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event update(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Page<Event> find(Event filter, Pageable pageable) {
        Example<Event> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return eventRepository.findAll(example, pageable);
//        return eventRepository.findByRegistrationOnEvent( filterDTO.getRegistration(), filterDTO.getEvent(), pageable );
    }

//    @Override
//    public Optional<Event> getEventByCodeEvent(String codeEvent) {
//        return eventRepository.findByEvent(codeEvent);
//    }

    private boolean existsByEvent (Event event) {
        return eventRepository.findByEventExistent(event.getTitle(), event.getEventStart(), event.getEventEnd(),
                event.getOrganizerId()).isPresent();
    }

//    @Override
//    public Page<Event> getRegistrationsByEvent(Registration registration, Pageable pageable) {
//        return eventRepository.findByRegistration(registration, pageable);
//    }

}
