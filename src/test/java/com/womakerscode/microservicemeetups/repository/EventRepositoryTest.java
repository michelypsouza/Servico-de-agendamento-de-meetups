package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.womakerscode.microservicemeetups.util.DateUtil.getDateWithZeroTime;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    EventRepository eventRepository;

//    @Test
//    @DisplayName("Must return true when an participant is registered for in the event.")
//    public void returnTrueWhenParticipantIsRegisteredInEvent() {
//
//        Long eventId = 1L;
//
//        Event event = createNewEvent(eventId);
//        entityManager.persist(event);
//
//        boolean exists = registrationRepository.findExistingRegistrationEvent(eventId, event.getParticipantId())
//                .isPresent();
//
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @DisplayName("Must return false when an participant is not registered for in the event.")
//    public void returnFalseWhenParticipantIsNotRegisteredInEvent() {
//
//        boolean exists = registrationRepository.findExistingRegistrationEvent(0L, 0L)
//                .isPresent();
//
//        assertThat(exists).isFalse();
//
//    }

    @Test
    @DisplayName("Should get an event by id from the base")
    public void findByIdTest() {

        Event newEvent = createNewEvent();
        entityManager.persist(newEvent);

        Optional<Event> foundEvent = eventRepository.findById(newEvent.getId());

        assertThat(foundEvent.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save an event from the base")
    public void saveEventTest() {

        Event newEvent = createNewEvent();

        Event savedEvent = eventRepository.save(newEvent);

        assertThat(savedEvent.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete and event from the base")
    public void deleteEvent() {

        Event newEvent = createNewEvent();
        entityManager.persist(newEvent);

        Event foundEvent = entityManager.find(Event.class, newEvent.getId());
        eventRepository.delete(foundEvent);

        Event deleteEvent = entityManager.find(Event.class, newEvent.getId());

        assertThat(deleteEvent).isNull();

    }

    public static Event createNewEvent() {
        return Event.builder()
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .eventStart(getDateWithZeroTime(2021,10,10))
                .eventEnd(getDateWithZeroTime(2021,10,10))
                .organizerId(3L)
                .build();
    }

}