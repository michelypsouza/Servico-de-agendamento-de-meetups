package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.repository.RegistrationRepository;
import com.womakerscode.microservicemeetups.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.womakerscode.microservicemeetups.util.DateUtil.getDateWithZeroTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    @MockBean
    RegistrationRepository registrationRepository;

    @BeforeEach
    public void setUp() {
        this.registrationService = new RegistrationServiceImpl(registrationRepository) ;
    }

    @Test
    @DisplayName("Should save an registration")
    public void saveRegistration() throws BusinessException {

        // cenario
        Registration registration = createValidRegistration();

        // execucao
        Mockito.when(registrationRepository.existsByRegistration(Mockito.anyString())).thenReturn(false);
        Mockito.when(registrationRepository.save(registration)).thenReturn(createValidRegistration());

        Registration savedRegistration = registrationService.save(registration);

        // assert
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getName()).isEqualTo("Michely Souza");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(getDateWithZeroTime(2021,10,11));
        assertThat(savedRegistration.getRegistrationNumber()).isEqualTo("001");

    }

    @Test
    @DisplayName("Should throw business error when try to save a new registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration();
        Mockito.when(registrationRepository.existsByRegistration(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable( () -> registrationService.save(registration));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created");

        Mockito.verify(registrationRepository, Mockito.never()).save(registration);

    }

    @Test
    @DisplayName("Should get an Registration by Id")
    public void getByRegistrationIdTest() {

        // cenario
        Integer id = 11;
        Registration registration = createValidRegistration();
        registration.setId(id);
        Mockito.when(registrationRepository.findById(id)).thenReturn(Optional.of(registration));

        // execucao
        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(foundRegistration.get().getRegistrationNumber()).isEqualTo(registration.getRegistrationNumber());

    }

    @Test
    @DisplayName("Should return empty when get an registration by id when doesn't exists")
    public void registrationNotFoundByIdTest(){

        Integer id = 11;
        Mockito.when(registrationRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Registration> registration = registrationService.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Should delete an registration")
    public void deleteRegistrationTest() {

        Registration registration = Registration.builder().id(11).build();

        assertDoesNotThrow(() -> registrationService.delete(registration));

        Mockito.verify(registrationRepository, Mockito.times(1)).delete(registration);

    }

    @Test
    @DisplayName("Should update an registration")
    public  void updateRegistration() {

        // cenario
        Integer id = 11;
        Registration updatingRegistration = Registration.builder().id(11).build();

        // execucao
        Registration updatedRegisttration = createValidRegistration();
        updatedRegisttration.setId(id);

        Mockito.when(registrationRepository.save(updatingRegistration)).thenReturn(updatedRegisttration);
        Registration registration = registrationService.update(updatingRegistration);

        // assert
        assertThat(registration.getId()).isEqualTo(updatedRegisttration.getId());
        assertThat(registration.getName()).isEqualTo(updatedRegisttration.getName());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegisttration.getDateOfRegistration());
        assertThat(registration.getRegistrationNumber()).isEqualTo(updatedRegisttration.getRegistrationNumber());

    }

    @Test
    @DisplayName("Should filter registrations must by properties")
    public void findRegistrationTest() {

        // cenario
        Registration registration = createValidRegistration();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Registration> listRegistrations = List.of(registration);
        Page<Registration> page = new PageImpl<>(List.of(registration),
                PageRequest.of(0,10), 1);

        // execucao
        Mockito.when(registrationRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Registration> result = registrationService.find(registration, pageRequest);

        // assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listRegistrations);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Should get an Registration model by registration attribute")
    public void getRegistrationByRegistrationAttribute() {

        String registrationAttribute = "1234";

        Mockito.when(registrationRepository.findByRegistration(registrationAttribute))
                .thenReturn(Optional.of(Registration.builder().id(11).registrationNumber(registrationAttribute).build()));

        Optional<Registration> registration = registrationService
                .getRegistrationByRegistrationNumber(registrationAttribute);

        assertThat(registration.isPresent()).isTrue();
        assertThat(registration.get().getId()).isEqualTo(11);
        assertThat(registration.get().getRegistrationNumber()).isEqualTo(registrationAttribute);

        Mockito.verify(registrationRepository, Mockito.times(1))
                .findByRegistration(registrationAttribute);

    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Michely Souza")
                .dateOfRegistration(getDateWithZeroTime(2021,10,11))
                .registrationNumber("001")
                .build();
    }

}
