package com.womakerscode.microservicemeetups.service;

import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
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

import java.util.List;
import java.util.Optional;

import static com.womakerscode.microservicemeetups.util.DateUtil.getDateWithZeroTime;
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

        // execucao
        Mockito.when(eventRepository.findByEventExistent(Mockito.anyString(), Mockito.any(), Mockito.any(),
                Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(eventRepository.save(event)).thenReturn(createValidEvent());
        Event savedEvent = eventService.save(event);

        // assert
        assertThat(savedEvent.getId()).isEqualTo(101L);
        assertThat(savedEvent.getTitle()).isEqualTo("Encontro Mulheres e Carreira em Tecnologia");
        assertThat(savedEvent.getDescription())
                .isEqualTo("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery");
        assertThat(savedEvent.getEventStart()).isEqualTo(getDateWithZeroTime(2021,10,10));
        assertThat(savedEvent.getEventEnd()).isEqualTo(getDateWithZeroTime(2021,10,10));
        assertThat(savedEvent.getOrganizerId()).isEqualTo(3L);

    }

    @Test
    @DisplayName("Should throw business error when thy " +
            "to save a new event with a event duplicated")
    public void shouldNotSaveAsEventDuplicated() {

        Event event = createValidEvent();
        Mockito.when(eventRepository.findByEventExistent(Mockito.anyString(), Mockito.any(), Mockito.any(),
                Mockito.anyLong())).thenReturn(Optional.of(event));

        Throwable exception = Assertions.catchThrowable( () -> eventService.save(event));
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
        event.setId(id);
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        // execucao
        Optional<Event> foundEvent = eventService.getById(id);

        // verificacao
        assertThat(foundEvent.isPresent()).isTrue();
        assertThat(foundEvent.get().getId()).isEqualTo(id);
        assertThat(foundEvent.get().getTitle()).isEqualTo(event.getTitle());
        assertThat(foundEvent.get().getDescription()).isEqualTo(event.getDescription());
        assertThat(foundEvent.get().getEventStart()).isEqualTo(event.getEventStart());
        assertThat(foundEvent.get().getEventEnd()).isEqualTo(event.getEventEnd());
        assertThat(foundEvent.get().getOrganizerId()).isEqualTo(event.getOrganizerId());

    }

    @Test
    @DisplayName("Should return empty when get an event by id when doesn't exists")
    public void eventNotFoundByIdTest() {
        Long id = 11L;
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Event> event  = eventService.getById(id);
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
        assertThat(event.getEventStart()).isEqualTo(updatedEvent.getEventStart());
        assertThat(event.getEventEnd()).isEqualTo(updatedEvent.getEventEnd());
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
    @DisplayName("Should check if the event already exists")
    public void getRegisteredEvent() {

        // cenario
        Event newEvent = createValidEvent();

        // execucao
        Mockito.when(eventRepository.findByEventExistent(newEvent.getTitle(), newEvent.getEventStart()
                , newEvent.getEventEnd(), newEvent.getOrganizerId()))
                .thenReturn(Optional.of(
                        Event.builder()
                                .id(101L)
                                .title("Encontro Mulheres e Carreira em Tecnologia")
                                .eventStart(getDateWithZeroTime(2021,10,10))
                                .eventEnd(getDateWithZeroTime(2021,10,10))
                                .organizerId(3L)
                                .build()
                ));
        Optional<Event> eventExistent = eventService.findByEventExistent(newEvent);

        // assert
        assertThat(eventExistent.isPresent()).isTrue();
        assertThat(eventExistent.get().getId()).isEqualTo(newEvent.getId());
        assertThat(eventExistent.get().getTitle()).isEqualTo(newEvent.getTitle());

        Mockito.verify(eventRepository, Mockito.times(1))
                .findByEventExistent(newEvent.getTitle(), newEvent.getEventStart(), newEvent.getEventEnd()
                        , newEvent.getOrganizerId());

    }

    private Event createValidEvent() {
        return Event.builder()
                .id(101L)
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .eventStart(getDateWithZeroTime(2021,10,10))
                .eventEnd(getDateWithZeroTime(2021,10,10))
                .organizerId(3L)
                .build();
    }

}