package com.womakerscode.microservicemeetups.controller;

import com.womakerscode.microservicemeetups.controller.dto.EventPostRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.EventPutRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.EventRequestFilter;
import com.womakerscode.microservicemeetups.controller.dto.EventResponse;
import com.womakerscode.microservicemeetups.controller.resource.EventController;
import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import com.womakerscode.microservicemeetups.service.EventService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.womakerscode.microservicemeetups.util.DateUtil.formatLocalDateTimeToStringWithTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {EventController.class})
@AutoConfigureMockMvc
public class EventControllerTest {

    static final String EVENT_API = "/api/event";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    @DisplayName("Should register an event")
    public void createEventTest() throws Exception {

        EventPostRequestBody dto = EventPostRequestBody.builder()
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .startDate(formatLocalDateTimeToStringWithTime(LocalDateTime.of(2022,3,24,19,0)))
                .endDate(formatLocalDateTimeToStringWithTime(LocalDateTime.of(2022,3,24,21,0)))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(1L)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Event event = Event.builder()
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .creationDate(LocalDateTime.now())
                .startDate(LocalDateTime.of(2022,3,24,19,0))
                .endDate(LocalDateTime.of(2022,3,24,21,0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(1L)
                .build();

        BDDMockito.given(eventService.save(Mockito.any(Event.class))).willReturn(event);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(EVENT_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("description").value(dto.getDescription()))
                .andExpect(jsonPath("startDate").value(dto.getStartDate()))
                .andExpect(jsonPath("endDate").value(dto.getEndDate()))
                .andExpect(jsonPath("eventTypeEnum").value(dto.getEventTypeEnum().name()))
                .andExpect(jsonPath("organizerId").value(dto.getOrganizerId()));

    }

    @Test
    @DisplayName("Should throw an exception when not have data enough for the test")
    public void invalidCreateEventTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new EventPostRequestBody());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(EVENT_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return error when trying to register an event already registered for the same organizer")
    public void  createEventErrorDuplicatedTest() throws Exception {

        Event event = createNewEvent();
        event.setId(105L);
        event.setOrganizerId(42L);

        EventPostRequestBody dto = EventPostRequestBody.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(formatLocalDateTimeToStringWithTime(event.getStartDate()))
                .endDate(formatLocalDateTimeToStringWithTime(event.getEndDate()))
                .eventTypeEnum(event.getEventTypeEnum())
                .organizerId(event.getOrganizerId())
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Event duplicatedEvent = createNewEvent();
        duplicatedEvent.setId(event.getId());
        duplicatedEvent.setOrganizerId(event.getOrganizerId());

        BDDMockito.given(eventService.findByEventExistent(event)).willReturn(Optional.of(duplicatedEvent));

        // procura na base se ja tem algum evento existente
        BDDMockito.given(eventService.save(Mockito.any(Event.class)))
                .willThrow(new BusinessException("Event already enrolled"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(EVENT_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get event information")
    public void getEventTest() throws Exception {

        Long id = 11L;
        Event event = createNewEvent();
        event.setId(id);
        event.setOrganizerId(21L);

        EventResponse eventResponse = EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .creationDate(formatLocalDateTimeToStringWithTime(event.getCreationDate()))
                .startDate(formatLocalDateTimeToStringWithTime(event.getStartDate()))
                .endDate(formatLocalDateTimeToStringWithTime(event.getEndDate()))
                .eventTypeEnum(event.getEventTypeEnum())
                .organizerId(event.getOrganizerId())
                .build();

        BDDMockito.given(eventService.getById(id)).willReturn(Optional.of(event));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EVENT_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(eventResponse.getId()))
                .andExpect(jsonPath("title").value(eventResponse.getTitle()))
                .andExpect(jsonPath("description").value(eventResponse.getDescription()))
                .andExpect(jsonPath("startDate").value(eventResponse.getStartDate()))
                .andExpect(jsonPath("endDate").value(eventResponse.getEndDate()))
                .andExpect(jsonPath("eventTypeEnum").value(eventResponse.getEventTypeEnum().name()))
                .andExpect(jsonPath("organizerId").value(eventResponse.getOrganizerId()));

    }

    @Test
    @DisplayName("Should return not found when the event doesn't exists")
    public void eventNotFoundTest() throws Exception {

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EVENT_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete the event")
    public void deleteEvent() throws Exception {

        BDDMockito.given(eventService.getById(anyLong()))
                .willReturn(Optional.of(Event.builder().id(101L).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(EVENT_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return resource not found when no event is found to delete")
    public void deleteNonExistentEventTest() throws Exception {

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(EVENT_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete the event")
    public void cannotDeleteWhenEventHasRegistrations() throws Exception {

        Event event = createNewEvent();
        event.setId(9L);
        event.setOrganizerId(5L);
        event.setRegistrations(List.of(Registration.builder().id(22L).build()));

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(event));
        BDDMockito.doThrow(new BusinessException("The event cannot be deleted as it has active registrations"))
                .when(eventService).validateEventWithRegistrationsForDelete(any(Event.class));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(EVENT_API.concat("/" + event.getId()))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update when event info")
    public void updateEventTest() throws Exception {

        Long eventId = 34L;
        Long organizerId = 3L;

        Event putEvent = createNewEvent();
        putEvent.setId(eventId);
        putEvent.setOrganizerId(organizerId);

        EventResponse eventPutResponse = EventResponse.builder()
                .id(putEvent.getId())
                .title(putEvent.getTitle())
                .description(putEvent.getDescription())
                .creationDate(formatLocalDateTimeToStringWithTime(putEvent.getCreationDate()))
                .startDate(formatLocalDateTimeToStringWithTime(putEvent.getStartDate()))
                .endDate(formatLocalDateTimeToStringWithTime(putEvent.getEndDate()))
                .eventTypeEnum(putEvent.getEventTypeEnum())
                .organizerId(putEvent.getOrganizerId())
                .build();

        EventPutRequestBody eventPutRequestBody = EventPutRequestBody.builder()
                .title(putEvent.getTitle())
                .description(putEvent.getDescription())
                .startDate(formatLocalDateTimeToStringWithTime(putEvent.getStartDate()))
                .endDate(formatLocalDateTimeToStringWithTime(putEvent.getEndDate()))
                .build();

        String json = new ObjectMapper().writeValueAsString(eventPutRequestBody);

        Event eventReturnedFromDatabaseForUpdate = Event.builder()
                .id(putEvent.getId())
                .title("título XXXX")
                .description("descrição XXXX ")
                .creationDate(putEvent.getCreationDate())
                .startDate(LocalDateTime.of(2022, 3, 10, 8, 30))
                .endDate(LocalDateTime.of(2022, 3, 12, 12, 0))
                .eventTypeEnum(putEvent.getEventTypeEnum())
                .organizerId(putEvent.getOrganizerId())
                .build();

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(eventReturnedFromDatabaseForUpdate));

        Event updatedEvent = createNewEvent();
        updatedEvent.setId(eventId);
        updatedEvent.setOrganizerId(organizerId);

        BDDMockito.given(eventService.update(eventReturnedFromDatabaseForUpdate)).willReturn(updatedEvent);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(EVENT_API.concat("/" + eventId))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(eventId))
                .andExpect(jsonPath("title").value(eventPutResponse.getTitle()))
                .andExpect(jsonPath("description").value(eventPutResponse.getDescription()))
                .andExpect(jsonPath("creationDate").value(eventPutResponse.getCreationDate()))
                .andExpect(jsonPath("startDate").value(eventPutResponse.getStartDate()))
                .andExpect(jsonPath("endDate").value(eventPutResponse.getEndDate()))
                .andExpect(jsonPath("eventTypeEnum").value(eventPutResponse.getEventTypeEnum().name()))
                .andExpect(jsonPath("organizerId").value(eventPutResponse.getOrganizerId()));

    }

    @Test
    @DisplayName("Should return 404 when try to update an event no existent")
    public void updateNonExistentEventTest() throws Exception {

        Event event = createNewEvent();
        event.setId(11L);
        event.setOrganizerId(21L);

        EventPutRequestBody eventPutRequestBody = EventPutRequestBody.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(formatLocalDateTimeToStringWithTime(event.getStartDate()))
                .endDate(formatLocalDateTimeToStringWithTime(event.getEndDate()))
                .build();

        String json = new ObjectMapper().writeValueAsString(eventPutRequestBody);
        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(EVENT_API.concat("/1"))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter event")
    public void findEventTest() throws Exception {

        Event event = Event.builder()
                .id(11L)
                .title("Womakerscode Dados")
                .description("Womakerscode Dados é um evento realizado pela Womakerscode sobre Banco de Dados")
                .creationDate(LocalDateTime.now())
                .startDate(LocalDateTime.of(2022,10,10,10,0))
                .endDate(LocalDateTime.of(2022,10,10,11,0))
                .eventTypeEnum(EventTypeEnum.ONLINE)
                .organizerId(1L)
                .build();
        EventRequestFilter eventRequestFilter = EventRequestFilter.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();

        BDDMockito.given(eventService.find(Mockito.any(Event.class), Mockito.any(Pageable.class)) )
                .willReturn(new PageImpl<Event>(List.of(event)
                        , PageRequest.of(0,100), 1));

        String queryString = String.format("?id=%d&title=%s&page=0&size=100",
                eventRequestFilter.getId(), eventRequestFilter.getTitle());


        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EVENT_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    private Event createNewEvent() {
        return Event.builder()
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .creationDate(LocalDateTime.now())
                .startDate(LocalDateTime.of(2022,3,24,19,0))
                .endDate(LocalDateTime.of(2022,3,24,21,0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .build();
    }

}
