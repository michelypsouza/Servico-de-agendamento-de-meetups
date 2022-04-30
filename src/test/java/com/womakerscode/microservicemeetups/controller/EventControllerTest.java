package com.womakerscode.microservicemeetups.controller;

import com.womakerscode.microservicemeetups.controller.dto.EventPostRequestBody;
import com.womakerscode.microservicemeetups.controller.resource.EventController;
import com.womakerscode.microservicemeetups.model.entity.Event;
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
                .title("Womakerscode Dados")
                .description("Palestra organizada pela Womakerscode sobre Dados")
                .startDate(LocalDateTime.of(2022,3,24,19,0))
                .endDate(LocalDateTime.of(2022,3,24,21,0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                .organizerId(1L)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Event event = Event.builder()
                .title("Womakerscode Dados")
                .description("Palestra organizada pela Womakerscode sobre Dados")
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
//                .andExpect(jsonPath("startDate")
//                        .value(formatLocalDateTimeToStringWithTime(dto.getStartDate())))
//                .andExpect(jsonPath("endDate")
//                        .value(formatLocalDateTimeToStringWithTime(dto.getEndDate())))
                //.andExpect(jsonPath("eventTypeEnum").value(dto.getEventTypeEnum().ordinal()))
                .andExpect(jsonPath("organizerId").value(dto.getOrganizerId().toString()));

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

    // testar duplicidade

//    @Test
//    @DisplayName("Should return error when try to register a registration already register on a meetup")
//    public void  meetupRegistrationErrorOnCreateMeetupTest() throws Exception {
//
//        EventPostRequestBody dto = EventPostRequestBody.builder().registrationAttribute("123").event("Womakerscode Dados").build();
//        String json = new ObjectMapper().writeValueAsString(dto);
//
//
//        Registration registration = Registration.builder().id(11).name("Ana Neri").registrationNumber("123").build();
//        BDDMockito.given(registrationService.getRegistrationByRegistrationNumber("123"))
//                .willReturn(Optional.of(registration));
//
//        // procura na base se ja tem algum registration pra esse meetup
//        BDDMockito.given(eventService.save(Mockito.any(Meetup.class))).willThrow(new BusinessException("Meetup already enrolled"));
//
//
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(EVENT_API)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isBadRequest());
//    }

    @Test
    @DisplayName("Should get event information")
    public void getEventTest() throws Exception {

        Long id = 11L;
        Event event = createNewEvent();
        event.setId(id);
        event.setOrganizerId(21L);

        BDDMockito.given(eventService.getById(id)).willReturn(Optional.of(event));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EVENT_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(event.getTitle()))
                .andExpect(jsonPath("description").value(event.getDescription()))
                .andExpect(jsonPath("startDate").value(event.getStartDate()))
                .andExpect(jsonPath("endDate").value(event.getEndDate()))
                //.andExpect(jsonPath("eventTypeEnum").value(event.getEventTypeEnum().ordinal()))
                .andExpect(jsonPath("organizerId")
                        .value(event.getOrganizerId()));

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
    @DisplayName("Should update when event info")
    public void updateEventTest() throws Exception {

        Long eventId = 34L;
        Long organizerId = 3L;
        String json = new ObjectMapper().writeValueAsString(createNewEvent());

        Event updatingEvent =
                Event.builder()
                        .id(eventId)
                        .title("título XXXX")
                        .description("descrição XXXX ")
                        .startDate(LocalDateTime.of(2022,3,10,8,30))
                        .endDate(LocalDateTime.of(2022,3,12,12,0))
                        .eventTypeEnum(EventTypeEnum.ONLINE)
                        .organizerId(organizerId)
                        .build();

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(updatingEvent));

        Event updatedEvent =
                Event.builder()
                        .id(eventId)
                        .title("Encontro Mulheres e Carreira em Tecnologia")
                        .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                        .startDate(LocalDateTime.of(2022,3,24,19,0))
                        .endDate(LocalDateTime.of(2022,3,24,21,0))
                        .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                        .organizerId(organizerId)
                        .build();

        BDDMockito.given(eventService.update(updatingEvent)).willReturn(updatedEvent);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(EVENT_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(eventId))
                .andExpect(jsonPath("title").value(createNewEvent().getTitle()))
                .andExpect(jsonPath("description").value(createNewEvent().getDescription()))
                .andExpect(jsonPath("startDate").value((createNewEvent().getStartDate())))
                .andExpect(jsonPath("endDate").value((createNewEvent().getEndDate())))
                //.andExpect(jsonPath("eventTypeEnum").value(createNewEvent().getEventTypeEnum().ordinal()))
                .andExpect(jsonPath("organizerId").value(organizerId));

    }

    @Test
    @DisplayName("Should return 404 when try to update an event no existent")
    public void updateNonExistentEventTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewEvent());
        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(EVENT_API.concat("/" + 1))
                .contentType(json)
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
                .startDate(LocalDateTime.of(2022,10,10,10,0))
                .endDate(LocalDateTime.of(2022,10,10,11,0))
                .eventTypeEnum(EventTypeEnum.ONLINE)
                .organizerId(1L)
                .build();

        BDDMockito.given(eventService.find(Mockito.any(Event.class), Mockito.any(Pageable.class)) )
                .willReturn(new PageImpl<Event>(List.of(event)
                        , PageRequest.of(0,100), 1));

//        String queryString = String.format("?title=%s&eventStart=%s&eventEnd=%s&organizerId=%d&page=0&size=100",
//                event.getTitle(), formatDateToString(event.getEventStart()), formatDateToString(event.getEventEnd()),
//        event.getTitle(), formatDateToString(event.getEventStart()), formatDateToString(event.getEventEnd()),
//                event.getOrganizerId());
        String queryString = String.format("?id=%d&title=%s&page=0&size=100",
                event.getId(), event.getTitle());


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
                //.id(101L)
                .title("Encontro Mulheres e Carreira em Tecnologia")
                .description("Mulheres e Carreira em Tecnologia parceria WoMakersCode e Zé Delivery")
                .startDate(LocalDateTime.of(2022,3,24,19,0))
                .endDate(LocalDateTime.of(2022,3,24,21,0))
                .eventTypeEnum(EventTypeEnum.FACE_TO_FACE)
                //.organizerId(3L)
                .build();
    }

}
