package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import com.womakerscode.microservicemeetups.repository.EventRepository;
import com.womakerscode.microservicemeetups.service.impl.EventServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EventServiceTest {

    EventService eventService;

    @MockBean
    EventRepository eventRepository;


    @BeforeEach
    public void setUp() {
        this.eventService = new EventServiceImpl(eventRepository);
    }

    @Test
    @DisplayName("Should save an event")
    public void saveEvent() {

        // cenario
        Event event = createValidEvent();
        Event returnedEvent = createValidEvent();

        // execucao
        Mockito.when(eventRepository.findByEventExistent(Mockito.anyString(), Mockito.any(), Mockito.any(),
                Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(eventRepository.save(event)).thenReturn(returnedEvent);
        Event savedEvent = eventService.save(event);

        // assert
        assertThat(savedEvent.getId()).isEqualTo(101L);
        assertThat(savedEvent.getTitle()).isEqualTo("Encontro Mulheres e Carreira em Tecnologia");
        assertThat(savedEvent.getDescription())
                .isEqualTo("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery");
        assertThat(savedEvent.getStartDate())
                .isEqualTo(LocalDateTime.of(2022,3,24,19,0));
        assertThat(savedEvent.getEndDate())
                .isEqualTo(LocalDateTime.of(2022,3,24,21,0));
        assertThat(savedEvent.getEventTypeEnum()).isEqualTo(EventTypeEnum.FACE_TO_FACE);
        assertThat(savedEvent.getOrganizerId()).isEqualTo(3L);

    }

    @Test
    @DisplayName("Should throw business error when thy to save a new event with a event duplicated")
    public void shouldNotSaveAsEventDuplicated() {

        Event event = createValidEvent();

        Mockito.when(eventRepository.findByEventExistent(Mockito.anyString(), Mockito.any(), Mockito.any(),
                Mockito.anyLong())).thenReturn(Optional.of(event));

        Throwable exception = Assertions.catchThrowable(() -> eventService.save(event));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Event already created");

        Mockito.verify(eventRepository, Mockito.never()).save(event);
    }

    @Test
    @DisplayName("Should get an Event by Id")
    public void getByEventIdTest() {

        // cenario
        Long id = 11L;
        Event event = createValidEvent();
        event.setId(11L);
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        // execucao
        Optional<Event> foundEvent = eventService.getById(id);

        // verificacao
        assertThat(foundEvent.isPresent()).isTrue();
        assertThat(foundEvent.get().getId()).isEqualTo(id);
        assertThat(foundEvent.get().getTitle()).isEqualTo(event.getTitle());
        assertThat(foundEvent.get().getDescription()).isEqualTo(event.getDescription());
        assertThat(foundEvent.get().getStartDate())
                .isEqualTo(event.getStartDate());
        assertThat(foundEvent.get().getEndDate())
                .isEqualTo(event.getEndDate());
        assertThat(foundEvent.get().getEventTypeEnum()).isEqualTo(EventTypeEnum.FACE_TO_FACE);
        assertThat(foundEvent.get().getOrganizerId()).isEqualTo(event.getOrganizerId());

    }

    @Test
    @DisplayName("Should return empty when get an event by id when doesn't exists")
    public void eventNotFoundByIdTest() {
        Long id = 11L;
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Event> event = eventService.getById(id);
        assertThat(event.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an event")
    public void deleteEventTest() {
        Event event = Event.builder().id(11L).build();
        assertDoesNotThrow(() -> eventService.delete(event));
        Mockito.verify(eventRepository, Mockito.times(1)).delete(event);
    }

    @Test
    @DisplayName("Should delete an event with invalid id")
    public void deleteEventTestInvalidId() {
        Event event = Event.builder().build();
        Throwable exception = Assertions.catchThrowable(() -> eventService.delete(event));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event id cannot be null");
        Mockito.verify(eventRepository, Mockito.never()).delete(event);
    }

    @Test
    @DisplayName("Test validate event with registrations")
    public void testValidateEventWithRegistrationsForDelete() {
        Event event = createValidEvent();
        event.setRegistrations(List.of(Registration.builder().id(22L).build()));
        //Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        Throwable exception = Assertions.catchThrowable(() -> eventService
                .validateEventWithRegistrationsForDelete(event));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("The event cannot be deleted as it has active registrations");
    }

    @Test
    @DisplayName("Should update an event")
    public void updateEvent() {

        // cenario
        Long id = 11L;
        Event updatingEvent = Event.builder().id(id).title("Encontro Mulheres e Carreira em Tecnologia 2022").build();

        // execucao
        Event updatedEvent = createValidEvent();

        updatedEvent.setId(id);
        updatedEvent.setTitle("Encontro Mulheres e Carreira em Tecnologia 2022");
        Mockito.when(eventRepository.save(updatingEvent)).thenReturn(updatedEvent);
        Event event = eventService.update(updatingEvent);

        // assert
        assertThat(event.getId()).isEqualTo(updatedEvent.getId());
        assertThat(event.getTitle()).isEqualTo(updatedEvent.getTitle());
        assertThat(event.getDescription()).isEqualTo(updatedEvent.getDescription());
        assertThat(event.getStartDate()).isEqualTo(updatedEvent.getStartDate());
        assertThat(event.getEndDate()).isEqualTo(updatedEvent.getEndDate());
        assertThat(event.getEventTypeEnum()).isEqualTo(updatedEvent.getEventTypeEnum());
        assertThat(event.getOrganizerId()).isEqualTo(updatedEvent.getOrganizerId());

    }

    @Test
    @DisplayName("Should filter events must by properties")
    public void findEventTest() {

        // cenario
        Event event = createValidEvent();
        PageRequest pageRequest = PageRequest.of(0,10);

        List<Event> listEvents = List.of(event);
        Page<Event> page = new PageImpl<Event>(List.of(event),
                PageRequest.of(0,10), 1);

        // execucao
        Mockito.when(eventRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Event> result = eventService.find(event, pageRequest);

        // assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listEvents);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should update an event with invalid id")
    public void updateEventTestInvalidId() {
        Event event = Event.builder().build();
        Throwable exception = Assertions.catchThrowable(() -> eventService.update(event));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event id cannot be null");
        Mockito.verify(eventRepository, Mockito.never()).save(event);
    }

    @Test
    @DisplayName("Should check if the event already exists")
    public void getRegisteredEvent() {

        // cenario
        Event newEvent = createValidEvent();

        // execucao
        Mockito.when(eventRepository.findByEventExistent(newEvent.getTitle(), newEvent.getStartDate()
                , newEvent.getEndDate(), newEvent.getOrganizerId()))
                .thenReturn(Optional.of(
                        Event.builder()
                                .id(101L)
                                .title("Encontro Mulheres e Carreira em Tecnologia")
                                .startDate(LocalDateTime.of(2022,3,24,19,0))
                                .endDate(LocalDateTime.of(2022,3,24,21,0))
                                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                                .organizerId(3L)
                                .build()
                ));
        Optional<Event> eventExistent = eventService.findByEventExistent(newEvent);

        // assert
        assertThat(eventExistent.isPresent()).isTrue();
        assertThat(eventExistent.get().getId()).isEqualTo(newEvent.getId());
        assertThat(eventExistent.get().getTitle()).isEqualTo(newEvent.getTitle());

        Mockito.verify(eventRepository, Mockito.times(1))
                .findByEventExistent(newEvent.getTitle(), newEvent.getStartDate(), newEvent.getEndDate()
                        , newEvent.getOrganizerId());

    }

    private Event createValidEvent() {
        return Event.builder()
                .id(101L)
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .startDate(LocalDateTime.of(2022,3,24,19,0))
                .endDate(LocalDateTime.of(2022,3,24,21,0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(3L)
                .build();
    }

}