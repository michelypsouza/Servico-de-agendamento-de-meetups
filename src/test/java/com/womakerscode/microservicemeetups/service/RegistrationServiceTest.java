package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    @MockBean
    RegistrationRepository registrationRepository;

    @BeforeEach
    public void setUp() {
        this.registrationService = new RegistrationServiceImpl(registrationRepository);
    }

    @Test
    @DisplayName("Should save an registration")
    public void saveRegistration() throws BusinessException {

        // cenario
        Registration registration = createValidRegistration(createValidEvent());

        // execucao
        Mockito.when(registrationRepository.findExistingRegistrationEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(registrationRepository.save(registration)).thenReturn(registration);

        Registration savedRegistration = registrationService.save(registration);

        // assert
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getNameTag()).isEqualTo("Michely Souza");
        assertThat(savedRegistration.getEvent()).isEqualTo(registration.getEvent());
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(savedRegistration.getParticipantId()).isEqualTo(registration.getParticipantId());

    }

    @Test
    @DisplayName("Should throw business error when try to save a new registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration(createValidEvent());
        Mockito.when(registrationRepository.findExistingRegistrationEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(registration));

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
        Registration registration = createValidRegistration(createValidEvent());
        Mockito.when(registrationRepository.findById(registration.getId())).thenReturn(Optional.of(registration));

        // execucao
        Optional<Registration> foundRegistration = registrationService.getRegistrationById(registration.getId());

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(registration.getId());
        assertThat(foundRegistration.get().getNameTag()).isEqualTo(registration.getNameTag());
        assertThat(foundRegistration.get().getEvent()).isEqualTo(registration.getEvent());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(foundRegistration.get().getParticipantId()).isEqualTo(registration.getParticipantId());

    }

    @Test
    @DisplayName("Should return empty when get an registration by id when doesn't exists")
    public void registrationNotFoundByIdTest(){

        Long id = 11L;
        Mockito.when(registrationRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Registration> registration = registrationService.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Should delete an registration")
    public void deleteRegistrationTest() {

        Registration registration = Registration.builder().id(11L).build();

        assertDoesNotThrow(() -> registrationService.delete(registration));

        Mockito.verify(registrationRepository, Mockito.times(1)).delete(registration);

    }

    @Test
    @DisplayName("Should update an registration")
    public  void updateRegistration() {

        // cenario
        Long id = 28L;
        Registration updatingRegistration = Registration.builder().id(28L).build();

        // execucao
        Registration updatedRegistration = createValidRegistration(createValidEvent());
        updatedRegistration.setId(id);

        Mockito.when(registrationRepository.save(updatingRegistration)).thenReturn(updatedRegistration);
        Registration registration = registrationService.update(updatingRegistration);

        // assert
        assertThat(registration.getId()).isEqualTo(updatedRegistration.getId());
        assertThat(registration.getNameTag()).isEqualTo(updatedRegistration.getNameTag());
        assertThat(registration.getEvent()).isEqualTo(updatedRegistration.getEvent());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(registration.getParticipantId()).isEqualTo(updatedRegistration.getParticipantId());

    }

    @Test
    @DisplayName("Should filter registrations must by properties")
    public void findRegistrationTest() {

        // cenario
        Registration registration = createValidRegistration(createValidEvent());
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

    private Event createValidEvent() {
        Long numberRandom = Math.abs(new Random().nextLong());
        return Event.builder()
                .id(numberRandom)
                .title("Encontro Mulheres e Carreira em Tecnologia " + numberRandom)
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .startDate(LocalDateTime.of(2022, 3, 24, 19, 0))
                .endDate(LocalDateTime.of(2022, 3, 24, 21, 0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(3L)
                .build();
    }

    private Registration createValidRegistration(Event event) {
        return Registration.builder()
                .id(101L)
                .nameTag("Michely Souza")
                .dateOfRegistration(LocalDateTime.now())
                .event(event)
                .participantId(23L)
                .build();
    }

}
