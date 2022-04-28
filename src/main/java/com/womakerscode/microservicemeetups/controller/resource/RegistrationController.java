package com.womakerscode.microservicemeetups.controller.resource;

import com.womakerscode.microservicemeetups.controller.dto.RegistrationFilterDTO;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationPostRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationRequest;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.service.EventService;
import com.womakerscode.microservicemeetups.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.womakerscode.microservicemeetups.util.DateUtil.getCurrentDate;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    private final EventService eventService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationRequest create(@RequestBody @Valid RegistrationPostRequestBody registrationPostRequestBody) {

        //Event event = eventService.getEventByCodeEvent(registrationPostRequestBody.getCodeEvent())
        Event event = eventService.getById(registrationPostRequestBody.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        Registration entity = Registration.builder()
                .nameTag(registrationPostRequestBody.getNameTag())
                .event(event)
                .participantId(registrationPostRequestBody.getParticipantId())
                .dateOfRegistration(getCurrentDate())
                .build();

        entity = registrationService.save(entity);
        return modelMapper.map(entity, RegistrationRequest.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationRequest get(@PathVariable Long id) {
        return registrationService
                .getRegistrationById(id)
                .map(registration -> modelMapper.map(registration, RegistrationRequest.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByRegistrationId(@PathVariable Long id) {
        Registration registration = registrationService.getRegistrationById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        registrationService.delete(registration);
    }

    @PutMapping("{id}")
    public RegistrationRequest update(@PathVariable Long id, RegistrationPostRequestBody registrationRequest) {

        Event event = eventService.getById(registrationRequest.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        return registrationService.getRegistrationById(id)
                .map(registration -> {
                    registration.setNameTag(registrationRequest.getNameTag());
                    registration.setEvent(event);
                    registration.setParticipantId(registrationRequest.getParticipantId());
                    registration = registrationService.update(registration);
                    return modelMapper.map(registration, RegistrationRequest.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<RegistrationRequest> find(RegistrationFilterDTO dto, Pageable pageRequest) {

        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = registrationService.find(filter, pageRequest);

        List<RegistrationRequest> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationRequest.class))
                .collect(Collectors.toList());

        return new PageImpl<RegistrationRequest>(list, pageRequest, result.getTotalElements());
    }

}
