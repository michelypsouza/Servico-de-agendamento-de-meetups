package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static com.womakerscode.microservicemeetups.util.DateUtil.getCurrentDate;

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

        Long eventId = 1L;

        Registration registration = createNewRegistration(eventId);
        entityManager.persist(registration);

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

        Registration newRegistration = createNewRegistration(1L);
        entityManager.persist(newRegistration);

        Optional<Registration> foundRegistration = registrationRepository.findById(newRegistration.getId());

        assertThat(foundRegistration.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save an registration from the base")
    public void saveRegistrationTest() {

        Registration newRegistration = createNewRegistration(1L);

        Registration savedRegistration = registrationRepository.save(newRegistration);

        assertThat(savedRegistration.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete and registration from the base")
    public void deleteRegistration() {

        Registration newRegistration = createNewRegistration(1L);
        entityManager.persist(newRegistration);

        Registration foundRegistration = entityManager.find(Registration.class, newRegistration.getId());
        registrationRepository.delete(foundRegistration);

        Registration deleteRegistration = entityManager.find(Registration.class, newRegistration.getId());

        assertThat(deleteRegistration).isNull();

    }

    public static Registration createNewRegistration(Long eventId) {
        return Registration.builder()
                .id(11L)
                .nameTag("Michely")
                .dateOfRegistration(getCurrentDate())
                .event(Event.builder()
                        .id(eventId)
                        .build())
                .participantId(23L)
                .build();
    }

}