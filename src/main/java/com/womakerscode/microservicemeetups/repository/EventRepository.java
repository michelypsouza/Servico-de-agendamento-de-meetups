package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = " select e from Event as e where e.title = :title and e.eventStart = :eventStart " +
            "and e.eventEnd = :eventEnd and e.eventProducerId = :eventProducerId ")
    Event findByEventExistent(@Param("title") String title,
                                    @Param("eventStart") Date eventStart,
                                    @Param("eventEnd") Date eventEnd,
                                    @Param("eventProducerId") Long eventProducerId);

//    @Query(value = " select m from Meetup as m join m.registration as r " +
//            "where r.registrationNumber = :registrationNumber or m.event = :event ")
//    Page<Event> findByRegistrationOnMeetup(
//            @Param("registrationNumber") String registrationNumber,
//            @Param("event") String event,
//            Pageable pageable
//    );
//
//    Page<Event> findByRegistration(Registration registration, Pageable pageable );

}