package com.womakerscode.microservicemeetups.controller;

import com.womakerscode.microservicemeetups.controller.dto.EventPostRequestBody;
import com.womakerscode.microservicemeetups.controller.resource.EventController;
import com.womakerscode.microservicemeetups.model.entity.Event;
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

import java.util.Arrays;

import static com.womakerscode.microservicemeetups.util.DateUtil.formatDateToString;
import static com.womakerscode.microservicemeetups.util.DateUtil.getDateWithZeroTime;
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
                .eventStart(getDateWithZeroTime(2021,10,10))
                .eventEnd(getDateWithZeroTime(2021,10,10))
                .organizerId(1L)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Event event = Event.builder()
                .title("Womakerscode Dados")
                .description("Palestra organizada pela Womakerscode sobre Dados")
                .eventStart(getDateWithZeroTime(2021,10,10))
                .eventEnd(getDateWithZeroTime(2021,10,10))
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
                .andExpect(jsonPath("eventStart").value(formatDateToString(dto.getEventStart())))
                .andExpect(jsonPath("eventEnd").value(formatDateToString(dto.getEventEnd())))
                .andExpect(jsonPath("eventProducerId").value(dto.getOrganizerId()));

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
    @DisplayName("Should filter event")
    public void findEventTest() throws Exception {

        Event event = Event.builder()
                .id(11L)
                .title("Womakerscode Dados")
                .description("Womakerscode Dados é um evento realizado pela Womakerscode sobre Banco de Dados")
                .eventStart(getDateWithZeroTime(2021,10,10))
                .eventEnd(getDateWithZeroTime(2021,10,10))
                .organizerId(1L)
                .build();

        BDDMockito.given(eventService.find(Mockito.any(Event.class), Mockito.any(Pageable.class)) )
                .willReturn(new PageImpl<Event>(Arrays.asList(event)
                        , PageRequest.of(0,100), 1));

        String queryString = String.format("?title=%s&eventStart=%s&eventEnd=%s&organizerId=%d&page=0&size=100",
                event.getTitle(), formatDateToString(event.getEventStart()), formatDateToString(event.getEventEnd()),
                event.getOrganizerId());


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

}
