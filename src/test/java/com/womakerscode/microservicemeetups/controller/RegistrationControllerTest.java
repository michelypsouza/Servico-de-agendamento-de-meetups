package com.womakerscode.microservicemeetups.controller;

import com.womakerscode.microservicemeetups.controller.dto.RegistrationPostRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationPutRequestBody;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationRequestFilter;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationResponse;
import com.womakerscode.microservicemeetups.controller.resource.RegistrationController;
import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Event;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.model.enumeration.EventTypeEnum;
import com.womakerscode.microservicemeetups.service.EventService;
import com.womakerscode.microservicemeetups.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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
import java.util.Random;

import static com.womakerscode.microservicemeetups.util.DateUtil.formatLocalDateTimeToStringWithTime;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    static String REGISTRATION_API = "/api/registration";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private EventService eventService;

    @Test
    @DisplayName("Should create a registration with success")
    public void createRegistrationTest() throws Exception {

        // cenario
        Event event = createValidEvent();
        Registration savedRegistration = Registration.builder()
                .id(155L)
                .nameTag("Michely Souza")
                .dateOfRegistration(LocalDateTime.of(2021, 10, 10, 13, 30))
                .event(event)
                .participantId(45L)
                .build();
        RegistrationPostRequestBody registrationPostRequestBody = RegistrationPostRequestBody.builder()
                .nameTag(savedRegistration.getNameTag())
                .participantId(savedRegistration.getParticipantId())
                .eventId(savedRegistration.getEvent().getId())
                .build();
        String json = new ObjectMapper().writeValueAsString(registrationPostRequestBody);

        // execucao
        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(savedRegistration.getEvent()));
        BDDMockito.given(registrationService.save(any(Registration.class))).willReturn(savedRegistration);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificacao, assert
        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(155L))
                .andExpect(jsonPath("nameTag").value(registrationPostRequestBody.getNameTag()))
                .andExpect(jsonPath("eventId").value(registrationPostRequestBody.getEventId()))
                .andExpect(jsonPath("dateOfRegistration")
                        .value(formatLocalDateTimeToStringWithTime(savedRegistration.getDateOfRegistration())))
                .andExpect(jsonPath("participantId").value(registrationPostRequestBody.getParticipantId()));

    }

    @Test
    @DisplayName("Should throw an exception when not have data enough for the test")
    public void createInvalidRegistrationTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(new RegistrationPostRequestBody());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should throw an exception when try to create a new registration with an registration already created.")
    public void createRegistrationWithDuplicatedRegistration() throws Exception {

        Event event = createValidEvent();
        Registration registration = createNewRegistration(event);

        RegistrationPostRequestBody dto = RegistrationPostRequestBody.builder()
                .nameTag(registration.getNameTag())
                .eventId(registration.getEvent().getId())
                .participantId(registration.getParticipantId())
                .build();

        String json  = new ObjectMapper().writeValueAsString(dto);

        Registration duplicatedRegistration = createNewRegistration(event);

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(event));

        BDDMockito.given(registrationService.findByExistingRegistrationForTheEvent(registration))
                .willReturn(Optional.of(duplicatedRegistration));

        BDDMockito.given(registrationService.save(any(Registration.class)))
                .willThrow(new BusinessException("Registration already created"));

        MockHttpServletRequestBuilder request  = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Registration already created"));
    }

    @Test
    @DisplayName("Should get registration information")
    public void getRegistrationTest() throws Exception {

        Registration registration = createNewRegistration(createValidEvent());
        Long id = registration.getId();

        RegistrationResponse response = RegistrationResponse.builder()
                .id(registration.getId())
                .nameTag(registration.getNameTag())
                .dateOfRegistration(formatLocalDateTimeToStringWithTime(registration.getDateOfRegistration()))
                .eventId(registration.getEvent().getId())
                .participantId(registration.getParticipantId())
                .build();

        BDDMockito.given(registrationService.getRegistrationById(id)).willReturn(Optional.of(registration));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(response.getId()))
                .andExpect(jsonPath("nameTag").value(response.getNameTag()))
                .andExpect(jsonPath("dateOfRegistration").value(response.getDateOfRegistration()))
                .andExpect(jsonPath("eventId").value(response.getEventId()))
                .andExpect(jsonPath("participantId").value(response.getParticipantId()));

    }

    @Test
    @DisplayName("Should return NOT FOUND when the registration doesn't exists")
    public void registrationNotFoundTest() throws Exception {

        BDDMockito.given(registrationService.getRegistrationById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete the registration")
    public void deleteRegistration() throws Exception {

        BDDMockito.given(registrationService.getRegistrationById(anyLong()))
                .willReturn(Optional.of(Registration.builder().id(11L).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return resource not found when no registration is found to delete")
    public void deleteNonExistentRegistrationTest() throws Exception {

        BDDMockito.given(registrationService.getRegistrationById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update when registration info")
    public void updateRegistrationTest() throws Exception {

        Long id = 3L;
        Event event = createValidEvent();
        Registration putRegistration = createNewRegistration(event);

        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .id(putRegistration.getId())
                .eventId(putRegistration.getEvent().getId())
                .participantId(putRegistration.getParticipantId())
                .nameTag(putRegistration.getNameTag())
                .dateOfRegistration(formatLocalDateTimeToStringWithTime(putRegistration.getDateOfRegistration()))
                .build();

        RegistrationPutRequestBody registrationPutRequestBody = RegistrationPutRequestBody.builder()
                .eventId(putRegistration.getEvent().getId())
                .participantId(putRegistration.getParticipantId())
                .nameTag(putRegistration.getNameTag())
                .build();

        String json = new ObjectMapper().writeValueAsString(registrationPutRequestBody);

        Registration registrationReturnedFromDatabaseForUpdate = Registration.builder()
                .id(id)
                .event(putRegistration.getEvent())
                .participantId(putRegistration.getParticipantId())
                .nameTag("Mel Souza")
                .dateOfRegistration(putRegistration.getDateOfRegistration())
                .build();

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(putRegistration.getEvent()));
        BDDMockito.given(registrationService.getRegistrationById(anyLong()))
                .willReturn(Optional.of(registrationReturnedFromDatabaseForUpdate));

        Registration updatedRegistration = createNewRegistration(event);

        BDDMockito.given(registrationService.update(registrationReturnedFromDatabaseForUpdate))
                .willReturn(updatedRegistration);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/" + id))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(registrationResponse.getId()))
                .andExpect(jsonPath("eventId").value(registrationResponse.getEventId()))
                .andExpect(jsonPath("participantId").value(registrationResponse.getParticipantId()))
                .andExpect(jsonPath("nameTag").value(registrationResponse.getNameTag()))
                .andExpect(jsonPath("dateOfRegistration")
                        .value(registrationResponse.getDateOfRegistration()));
    }

    @Test
    @DisplayName("Should return 404 when try to update an registration no existent")
    public void updateNonExistentRegistrationTest() throws Exception {

        Registration registration = createNewRegistration(createValidEvent());
        RegistrationPutRequestBody registrationPutRequestBody = RegistrationPutRequestBody.builder()
                .eventId(registration.getEvent().getId())
                .participantId(registration.getParticipantId())
                .nameTag(registration.getNameTag())
                .build();
        String json = new ObjectMapper().writeValueAsString(registrationPutRequestBody);

        BDDMockito.given(eventService.getById(anyLong())).willReturn(Optional.of(registration.getEvent()));
        BDDMockito.given(registrationService.getRegistrationById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/1"))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter registration")
    public void findRegistrationTest() throws Exception {

        Registration registration = createNewRegistration(createValidEvent());
        RegistrationRequestFilter registrationRequestFilter = RegistrationRequestFilter.builder()
                .id(registration.getId())
                .nameTag(registration.getNameTag())
                .dateOfRegistration(formatLocalDateTimeToStringWithTime(registration.getDateOfRegistration()))
                .eventId(registration.getEvent().getId())
                .participantId(registration.getParticipantId())
                .build();

        BDDMockito.given(registrationService.find(any(Registration.class), any(Pageable.class)) )
                .willReturn(new PageImpl<Registration>(List.of(registration)
                        , PageRequest.of(0,100), 1));

        String queryString = String.format("?nameTag=%s&dateOfRegistration=%s&participantId=%d&page=0&size=100"
                , registrationRequestFilter.getNameTag(), registrationRequestFilter.getDateOfRegistration()
                , registrationRequestFilter.getParticipantId());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    private Registration createNewRegistration(Event event) {
        return Registration.builder()
                .id(101L)
                .nameTag("Michely Souza")
                .dateOfRegistration(LocalDateTime.now())
                .event(event)
                .participantId(25L)
                .build();
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

}
