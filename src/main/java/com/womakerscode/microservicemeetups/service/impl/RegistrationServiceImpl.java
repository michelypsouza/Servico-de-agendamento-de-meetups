package com.womakerscode.microservicemeetups.service.impl;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.repository.RegistrationRepository;
import com.womakerscode.microservicemeetups.service.RegistrationService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    RegistrationRepository registrationRepository;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration save(Registration registration) {
        if (registrationRepository.existsByRegistration(registration.getRegistration())) {
            throw new BusinessException("Registration already created");
        }
        return registrationRepository.save(registration);
    }

    @Override
    public Optional<Registration> getRegistrationById(Integer id) {
        return registrationRepository.findById(id);
    }

    @Override
    public void delete(Registration registration) {
        if (registration == null || registration.getId() == null) {
            throw new IllegalArgumentException("Registration id cannot be null");
        }
        registrationRepository.delete(registration);
    }

    @Override
    public Registration update(Registration registration) {
        if (registration == null || registration.getId() == null) {
            throw new IllegalArgumentException("Registration id cannot be null");
        }
        return registrationRepository.save(registration);
    }

    @Override
    public Page<Registration> find(Registration filter, Pageable pageable) {
        Example<Registration> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return registrationRepository.findAll(example, pageable);
    }

    @Override
    public Optional<Registration> getRegistrationByRegistrationAttribute(String registrationAttribute) {
        return registrationRepository.findByRegistration(registrationAttribute);
    }
}
