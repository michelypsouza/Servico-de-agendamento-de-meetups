package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventService {

    Event save(Event event);

    Optional<Event> getById(Long id);

    void delete(Event event);

    Event update(Event event);

    Page<Event> find(Event filter, Pageable pageable);

    Optional<Event> findByEventExistent(Event event);

//    Optional<Event> getEventByCodeEvent(String codeEvent);

//    Page<Event> getRegistrationsByEvent(Registration registration, Pageable pageable);

}
