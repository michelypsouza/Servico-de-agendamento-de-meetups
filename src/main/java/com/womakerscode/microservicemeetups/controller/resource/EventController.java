package com.womakerscode.microservicemeetups.controller.resource;

import com.womakerscode.microservicemeetups.controller.dto.EventRequest;
import com.womakerscode.microservicemeetups.controller.dto.EventPostRequestBody;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.service.EventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public EventRequest create(@RequestBody EventPostRequestBody eventPostRequestBody) {

        Event entity = Event.builder()
                .title(eventPostRequestBody.getTitle())
                .description(eventPostRequestBody.getDescription())
                .eventStart(eventPostRequestBody.getEventStart())
                .eventEnd(eventPostRequestBody.getEventEnd())
                .organizerId(eventPostRequestBody.getOrganizerId())
                .build();

        entity = eventService.save(entity);
        return modelMapper.map(entity, EventRequest.class);
    }

    @GetMapping
    public Page<EventRequest> find(EventRequest eventRequest, Pageable pageRequest) {
        Event filter = modelMapper.map(eventRequest, Event.class);
        Page<Event> result = eventService.find(filter, pageRequest);
        List<EventRequest> events = result
                .getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, EventRequest.class)).collect(Collectors.toList());
        return new PageImpl<EventRequest>(events, pageRequest, result.getTotalElements());
    }

}
