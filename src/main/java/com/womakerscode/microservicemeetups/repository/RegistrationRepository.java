package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @Query(value = " select r from Registration as r join r.event as e " +
            "where e.id = :eventId and r.participantId = :participantId ")
    Optional<Registration> findExistingRegistrationEvent(@Param("eventId") Long eventId,
                              @Param("participantId") Long participantId);

//    boolean existsByRegistration(String registrationNumber);

//    Optional<Registration> findByRegistration(String registrationNumber);
}
