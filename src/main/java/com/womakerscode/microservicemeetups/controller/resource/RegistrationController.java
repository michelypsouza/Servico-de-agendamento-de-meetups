package com.womakerscode.microservicemeetups.controller.resource;

import com.womakerscode.microservicemeetups.controller.dto.RegistrationPostRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationPutRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationRequestFilter;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    private final EventService eventService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse create(@RequestBody @Valid RegistrationPostRequestBody registrationPostRequestBody) {

        Event event = eventService.getById(registrationPostRequestBody.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        Registration entity = Registration.builder()
                .nameTag(registrationPostRequestBody.getNameTag())
                .event(event)
                .participantId(registrationPostRequestBody.getParticipantId())
                .dateOfRegistration(LocalDateTime.now())
                .build();

        entity = registrationService.save(entity);
        return modelMapper.map(entity, RegistrationResponse.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationResponse get(@PathVariable Long id) {
        return registrationService
                .getRegistrationById(id)
                .map(registration -> {
                    //modelMapper.map(registration, RegistrationResponse.class);
                    registration.setEvent(modelMapper.map(registration.getEvent(), Event.class));
                    return modelMapper.map(registration, RegistrationResponse.class);
                })
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
    public RegistrationResponse update(@PathVariable Long id
            , @RequestBody @Valid RegistrationPutRequestBody registrationRequest) {

        return registrationService.getRegistrationById(id)
                .map(registration -> {
                    // so pode alterar o nome do cracha
                    registration.setNameTag(registrationRequest.getNameTag());
                    registration = registrationService.update(registration);
                    return modelMapper.map(registration, RegistrationResponse.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<RegistrationResponse> find(RegistrationRequestFilter dto, Pageable pageRequest) {

        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = registrationService.find(filter, pageRequest);

        List<RegistrationResponse> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<RegistrationResponse>(list, pageRequest, result.getTotalElements());
    }

}
