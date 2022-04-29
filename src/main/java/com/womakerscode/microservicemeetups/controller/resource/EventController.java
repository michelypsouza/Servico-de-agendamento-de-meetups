package com.womakerscode.microservicemeetups.controller.resource;

import com.womakerscode.microservicemeetups.controller.dto.EventPostRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.EventPutRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.EventRequest;
import com.womakerscode.microservicemeetups.controller.dto.EventResponse;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.service.EventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@RequestBody @Valid EventPostRequestBody eventPostRequestBody) {
        Event entity = modelMapper.map(eventPostRequestBody, Event.class);
        entity.setCreationDate(LocalDateTime.now());
        entity = eventService.save(entity);
        return modelMapper.map(entity, EventResponse.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse get(@PathVariable Long id) {
        return eventService
                .getById(id)
                .map(event -> modelMapper.map(event, EventResponse.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Event event = eventService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        eventService.delete(event);
    }

    @PutMapping("{id}")
    public EventResponse update(@PathVariable Long id, EventPutRequestBody eventPutRequestBody) {
        return eventService.getById(id)
                .map(event -> {
                    event.setTitle(eventPutRequestBody.getTitle());
                    event.setDescription(eventPutRequestBody.getDescription());
                    event.setStartDate(eventPutRequestBody.getStartDate());
                    event.setEndDate(eventPutRequestBody.getEndDate());
                    event = eventService.update(event);
                    return modelMapper.map(event, EventResponse.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<EventResponse> find(EventRequest eventRequest, Pageable pageRequest) {
        Event filter = modelMapper.map(eventRequest, Event.class);
        Page<Event> result = eventService.find(filter, pageRequest);
        List<EventResponse> events = result
                .getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, EventResponse.class)).collect(Collectors.toList());
        return new PageImpl<EventResponse>(events, pageRequest, result.getTotalElements());
    }

}
