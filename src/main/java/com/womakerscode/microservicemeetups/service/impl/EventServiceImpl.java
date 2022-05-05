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
    public void delete(Event event) {
        if (event == null || event.getId() == null) {
            throw new IllegalArgumentException("Event id cannot be null");
        }
        eventRepository.delete(event);
    }

    @Override
    public void validateEventWithRegistrationsForDelete(Event event) {
        //buscando lista de inscrições do evento no banco de dados
        boolean hasRegistrationOnEvent = event.getRegistrations() != null || !event.getRegistrations().isEmpty();
        if (hasRegistrationOnEvent) {
            throw new BusinessException("The event cannot be deleted as it has active registrations");
        }

    }

    //TODO: inserir mais uma validacao no save();
    @Override
    public Event update(Event event) {
        if (event == null || event.getId() == null) {
            throw new IllegalArgumentException("Event id cannot be null");
        }
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
    }

    @Override
    public Optional<Event> findByEventExistent (Event event) {
        return eventRepository.findByEventExistent(event.getTitle(), event.getStartDate(), event.getEndDate(),
                event.getOrganizerId());
    }

    private boolean existsByEvent (Event event) {
        return findByEventExistent(event).isPresent();
    }

}
