package com.womakerscode.microservicemeetups.repository;

import com.womakerscode.microservicemeetups.model.entity.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

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
    @DisplayName("Should return true when exists an registration already created.")
    public void returnTrueWhenRegistrationExists() {

        String registration = "123";

        Registration registrationClassAttribute = createNewRegistration(registration);
        entityManager.persist(registrationClassAttribute);

        boolean exists = registrationRepository.existsByRegistration(registration);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when doesn't exists an registration_attribute with a " +
            "registration already created.")
    public void returnFalseWhenRegistrationAttributeDoesntExists() {

        String registration = "123";

        boolean exists = registrationRepository.existsByRegistration(registration);

        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Should get an registration by id from the base")
    public void findByIdTest() {

        Registration registrationClassAttribute = createNewRegistration("323");
        entityManager.persist(registrationClassAttribute);

        Optional<Registration> foundRegistration = registrationRepository
                .findById(registrationClassAttribute.getId());

        assertThat(foundRegistration.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save an registration from the base")
    public void saveRegistrationTest() {

        Registration registrationClassAttribute = createNewRegistration("323");

        Registration savedRegistration = registrationRepository.save(registrationClassAttribute);

        assertThat(savedRegistration.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete and registration from the base")
    public void deleteRegistration() {

        Registration registrationClassAttribute = createNewRegistration("323");
        entityManager.persist(registrationClassAttribute);

        Registration foundRegistration = entityManager
                .find(Registration.class, registrationClassAttribute.getId());
        registrationRepository.delete(foundRegistration);

        Registration deleteRegistration = entityManager
                .find(Registration.class, registrationClassAttribute.getId());

        assertThat(deleteRegistration).isNull();

    }

    public static Registration createNewRegistration(String registration) {
        return Registration.builder()
                .name("Michely Souza")
                .dateOfRegistration(Calendar.getInstance(Locale.getDefault()).getTime())
//                .dateOfRegistration(LocalDate.now())
                .registration(registration).build();
    }

}