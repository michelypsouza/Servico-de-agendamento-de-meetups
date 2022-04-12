package com.womakerscode.microservicemeetups.controller.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationDTO;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.service.RegistrationService;
import com.womakerscode.microservicemeetups.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    //@Autowired
    private RegistrationService registrationService;

    //@Autowired
    private ModelMapper modelMapper;

    public RegistrationController (RegistrationService registrationService, ModelMapper modelMapper) {
        this.registrationService = registrationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationDTO create(@RequestBody @Valid RegistrationDTO registrationDTO) {
        Registration entity = modelMapper.map(registrationDTO, Registration.class);
        entity = registrationService.save(entity);
        return modelMapper.map(entity, RegistrationDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTO get(@PathVariable Integer id) {
        return registrationService
                .getRegistrationById(id)
                .map(registration -> modelMapper.map(registration, RegistrationDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByRegistrationId(@PathVariable Integer id) {
        Registration registration = registrationService.getRegistrationById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        registrationService.delete(registration);
    }


    @PutMapping("{id}")
    public RegistrationDTO update(@PathVariable Integer id, RegistrationDTO registrationDTO) {

        return registrationService.getRegistrationById(id)
                .map(registration -> {
                    registration.setName(registrationDTO.getName());
                    registration.setDateOfRegistration(
                            DateUtil.convertStringToLocalDate(registrationDTO.getDateOfRegistration()));
                    //registration.setDateOfRegistration(registrationDTO.getDateOfRegistration());
                    registration = registrationService.update(registration);

                    return modelMapper.map(registration, RegistrationDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    //public Page<RegistrationDTO> find(RegistrationDTO dto, Pageable pageable) {
    public Page<RegistrationDTO> find(@RequestParam(required = false) Map<String,String> mapAttributesRegistration
            , Pageable pageable) {

        RegistrationDTO dto = attributesRegistrationToDTO(mapAttributesRegistration);
        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = registrationService.find(filter, pageable);

        List<RegistrationDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<RegistrationDTO>(list, pageable, result.getTotalElements());
    }

    private RegistrationDTO attributesRegistrationToDTO(Map<String,String> mapAttributesRegistration) {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(mapAttributesRegistration, RegistrationDTO.class);
    }

}
