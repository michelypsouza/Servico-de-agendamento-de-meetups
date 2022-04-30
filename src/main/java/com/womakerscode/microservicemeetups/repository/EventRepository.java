package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = " select e from Event as e where e.title = :title and e.startDate = :startDate " +
            "and e.endDate = :endDate and e.organizerId = :organizerId ")
    Optional<Event> findByEventExistent(@Param("title") String title,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("organizerId") Long organizerId);

}