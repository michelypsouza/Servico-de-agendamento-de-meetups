package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface RegistrationService {

    Registration save(Registration registration);

    Optional<Registration> getRegistrationById(Integer id);

    void delete(Registration registration);

    Registration update(Registration registration);

    Page<Registration> find(Registration filter, PageRequest pageRequest);

    Optional<Registration> getRegistrationByRegistrationAttribute(String registrationAttribute);
}
