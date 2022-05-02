package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RegistrationRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RegistrationRepository registrationRepository;

    @Test
    @DisplayName("Must return true when an participant is registered for in the event.")
    public void returnTrueWhenParticipantIsRegisteredInEvent() {

        Registration registration = createNewRegistration(persistEvent());
        Registration savedRegistration = registrationRepository.save(registration);

        Long eventId = savedRegistration.getEvent().getId();

        boolean exists = registrationRepository.findExistingRegistrationEvent(eventId, registration.getParticipantId())
                .isPresent();

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Must return false when an participant is not registered for in the event.")
    public void returnFalseWhenParticipantIsNotRegisteredInEvent() {

        boolean exists = registrationRepository.findExistingRegistrationEvent(0L, 0L)
                .isPresent();

        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Should get an registration by id from the base")
    public void findByIdTest() {

        Registration newRegistration = createNewRegistration(persistEvent());
        Registration savedRegistration = registrationRepository.save(newRegistration);

        Optional<Registration> foundRegistration = registrationRepository.findById(savedRegistration.getId());
        //Registration foundRegistration = entityManager.find(Registration.class, savedRegistration.getId());

        assertThat(foundRegistration.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save an registration from the base")
    public void saveRegistrationTest() {

        Registration newRegistration = createNewRegistration(persistEvent());

        Registration savedRegistration = registrationRepository.save(newRegistration);

        assertThat(savedRegistration.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete and registration from the base")
    public void deleteRegistration() {

        Registration newRegistration = createNewRegistration(persistEvent());
        Registration savedRegistration = registrationRepository.save(newRegistration);

        Registration foundRegistration = entityManager.find(Registration.class, savedRegistration.getId());
        registrationRepository.delete(foundRegistration);

        Registration deleteRegistration = entityManager.find(Registration.class, newRegistration.getId());

        assertThat(deleteRegistration).isNull();

    }

    private Event persistEvent() {
        long numberRandom = Math.abs(new Random().nextLong());
        Event event = Event.builder()
//                .id(numberRandom)
                .title("Encontro Mulheres e Carreira em Tecnologia "+ Long.toString(numberRandom))
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .startDate(LocalDateTime.of(2022, 3, 24, 19, 0))
                .endDate(LocalDateTime.of(2022, 3, 24, 21, 0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(3L)
                .build();
        return entityManager.persist(event);
    }

    public static Registration createNewRegistration(Event event) {
        return Registration.builder()
                .id(11L)
                .nameTag("Michely")
                .dateOfRegistration(LocalDateTime.now())
                .event(event)
                .participantId(23L)
                .build();
    }

}