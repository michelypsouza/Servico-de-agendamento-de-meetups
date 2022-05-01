package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import com.womakerscode.microservicemeetups.repository.EventRepository;
import com.womakerscode.microservicemeetups.repository.RegistrationRepository;
import com.womakerscode.microservicemeetups.service.impl.EventServiceImpl;
import com.womakerscode.microservicemeetups.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    EventService eventService;

    @MockBean
    RegistrationRepository registrationRepository;

    @MockBean
    EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        this.registrationService = new RegistrationServiceImpl(registrationRepository);
        this.eventService = new EventServiceImpl(eventRepository);
    }

    @Test
    @DisplayName("Should save an registration")
    public void saveRegistration() throws BusinessException {

        // cenario
        Registration registration = createValidRegistration(persistEvent());

        //Optional<Event> returnedRegistration = eventRepository.findById(registration.getEvent().getId());

        // execucao
        Mockito.when(registrationRepository.findExistingRegistrationEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(registrationRepository.save(registration)).thenReturn(registration);

        Registration savedRegistration = registrationService.save(registration);

        // assert
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getNameTag()).isEqualTo("Michely Souza");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDateTime.now());
//        assertThat(savedRegistration.getRegistrationNumber()).isEqualTo("001");

    }

    @Test
    @DisplayName("Should throw business error when try to save a new registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration(persistEvent());
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
//        Long id = 11L;
        Registration registration = createValidRegistration(persistEvent());
        Long id = registration.getId();
        registration.setId(id);
        Mockito.when(registrationRepository.findById(id)).thenReturn(Optional.of(registration));

        // execucao
        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getNameTag()).isEqualTo(registration.getNameTag());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
//        assertThat(foundRegistration.get().getRegistrationNumber()).isEqualTo(registration.getRegistrationNumber());

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

//    @Test
//    @DisplayName("Should update an registration")
//    public  void updateRegistration() {
//
//        // cenario
//        Integer id = 11;
//        Registration updatingRegistration = Registration.builder().id(11).build();
//
//        // execucao
//        Registration updatedRegisttration = createValidRegistration();
//        updatedRegisttration.setId(id);
//
//        Mockito.when(registrationRepository.save(updatingRegistration)).thenReturn(updatedRegisttration);
//        Registration registration = registrationService.update(updatingRegistration);
//
//        // assert
//        assertThat(registration.getId()).isEqualTo(updatedRegisttration.getId());
//        assertThat(registration.getName()).isEqualTo(updatedRegisttration.getName());
//        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegisttration.getDateOfRegistration());
//        assertThat(registration.getRegistrationNumber()).isEqualTo(updatedRegisttration.getRegistrationNumber());
//
//    }
//
//    @Test
//    @DisplayName("Should filter registrations must by properties")
//    public void findRegistrationTest() {
//
//        // cenario
//        Registration registration = createValidRegistration();
//        PageRequest pageRequest = PageRequest.of(0, 10);
//
//        List<Registration> listRegistrations = List.of(registration);
//        Page<Registration> page = new PageImpl<>(List.of(registration),
//                PageRequest.of(0,10), 1);
//
//        // execucao
//        Mockito.when(registrationRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
//                .thenReturn(page);
//
//        Page<Registration> result = registrationService.find(registration, pageRequest);
//
//        // assert
//        assertThat(result.getTotalElements()).isEqualTo(1);
//        assertThat(result.getContent()).isEqualTo(listRegistrations);
//        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
//        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
//
//    }
//
//    @Test
//    @DisplayName("Should get an Registration model by registration attribute")
//    public void getRegistrationByRegistrationAttribute() {
//
//        String registrationAttribute = "1234";
//
//        Mockito.when(registrationRepository.findByRegistration(registrationAttribute))
//                .thenReturn(Optional.of(Registration.builder().id(11).registrationNumber(registrationAttribute).build()));
//
//        Optional<Registration> registration = registrationService
//                .getRegistrationByRegistrationNumber(registrationAttribute);
//
//        assertThat(registration.isPresent()).isTrue();
//        assertThat(registration.get().getId()).isEqualTo(11);
//        assertThat(registration.get().getRegistrationNumber()).isEqualTo(registrationAttribute);
//
//        Mockito.verify(registrationRepository, Mockito.times(1))
//                .findByRegistration(registrationAttribute);
//
//    }

    private Event persistEvent() {
        Long numberRandom = new Random().nextLong();
        Event event = Event.builder()
                .id(numberRandom)
                .title("Encontro Mulheres e Carreira em Tecnologia "+numberRandom.toString())
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .startDate(LocalDateTime.of(2022, 3, 24, 19, 0))
                .endDate(LocalDateTime.of(2022, 3, 24, 21, 0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(3L)
                .build();
        return eventRepository.save(event);
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
