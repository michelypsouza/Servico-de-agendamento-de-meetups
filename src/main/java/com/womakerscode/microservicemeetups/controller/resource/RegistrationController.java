package com.womakerscode.microservicemeetups.controller.resource;

import com.womakerscode.microservicemeetups.controller.dto.RegistrationFilterDTO;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationRequest;
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
import org.springframework.web.bind.annotation.*;
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
    public RegistrationResponse create(@RequestBody @Valid RegistrationRequestBody registrationPostRequestBody) {

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
                .map(registration -> modelMapper.map(registration, RegistrationResponse.class))
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
            , @RequestBody @Valid RegistrationRequestBody registrationRequest) {

        Event event = eventService.getById(registrationRequest.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        return registrationService.getRegistrationById(id)
                .map(registration -> {
                    registration.setNameTag(registrationRequest.getNameTag());
                    registration.setEvent(event);
                    registration.setParticipantId(registrationRequest.getParticipantId());
                    registration = registrationService.update(registration);
                    return modelMapper.map(registration, RegistrationResponse.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<RegistrationResponse> find(RegistrationFilterDTO dto, Pageable pageRequest) {

        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = registrationService.find(filter, pageRequest);

        List<RegistrationResponse> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<RegistrationResponse>(list, pageRequest, result.getTotalElements());
    }

    //    @GetMapping
//    //public Page<RegistrationDTO> find(RegistrationDTO dto, Pageable pageable) {
//    public Page<RegistrationDTO> find(@RequestParam(required = false) Map<String,String> mapAttributesRegistration
//            , Pageable pageable) {
//
//        RegistrationDTO dto = attributesRegistrationToDTO(mapAttributesRegistration);
//        Registration filter = modelMapper.map(dto, Registration.class);
//        Page<Registration> result = registrationService.find(filter, pageable);
//
//        List<RegistrationDTO> list = result.getContent()
//                .stream()
//                .map(entity -> modelMapper.map(entity, RegistrationDTO.class))
//                .collect(Collectors.toList());
//
//        return new PageImpl<RegistrationDTO>(list, pageable, result.getTotalElements());
//    }

//    private RegistrationDTO attributesRegistrationToDTO(Map<String,String> mapAttributesRegistration) {
//        final ObjectMapper mapper = new ObjectMapper();
//        DateFormat df = new SimpleDateFormat(DateUtil.DATE_PATTERN_DEFAULT);
//        mapper.setDateFormat(df);
//        return mapper.convertValue(mapAttributesRegistration, RegistrationDTO.class);
//    }

}
